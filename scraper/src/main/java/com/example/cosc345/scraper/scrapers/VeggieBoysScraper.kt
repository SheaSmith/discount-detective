package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.VeggieBoysApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*

class VeggieBoysScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val baseUrl = "https://veggieboys.co.nz"
        val veggieBoysService = generateHtmlRequest(VeggieBoysApi::class.java, baseUrl)
        val retailerId = "veggie-boys"

        val stores = listOf(
            Store(
                "${retailerId}-south",
                "Veggie Boys South Dunedin",
                "16 Prince Albert Road, St Kilda, Dunedin 9012",
                -45.90009,
                170.503682,
                true
            ),
            Store(
                "${retailerId}-north",
                "Veggie Boys North Dunedin",
                "16 Prince Albert Road, St Kilda, Dunedin 9012",
                -45.867877,
                170.514351,
                true
            )
        )

        val products = mutableListOf<RetailerProductInformation>()

        var dairyDaleMultiBuy: Double? = null

        veggieBoysService.getProducts().products!!.forEach { veggieBoysProduct ->
            if (veggieBoysProduct.id == "275") {
                dairyDaleMultiBuy = veggieBoysProduct.price
            } else {

                val product = RetailerProductInformation(
                    retailer = retailerId,
                    id = veggieBoysProduct.id,
                    saleType = SaleType.EACH
                )

                var name = veggieBoysProduct.name!!
                arrayOf(
                    Units.GRAMS,
                    Units.KILOGRAMS,
                    Units.LITRES,
                    Units.MILLILITRES,
                    Units.PACK
                ).forEach {
                    val result = extractAndRemoveQuantity(name, it)

                    name = result.first
                    if (result.second != null) {
                        product.quantity = "${result.second}${it}"

                        if (it == Units.GRAMS) {
                            product.weight = result.second?.toInt()
                        } else if (it == Units.KILOGRAMS) {
                            product.weight = result.second?.times(1000)?.toInt()
                        }
                    }
                }

                name = name.replace("Milk - ", "", ignoreCase = true)
                    .replace("loose", "", ignoreCase = true)
                    .replace("Biscuits - ", "", ignoreCase = true)
                    .replace("Cream - ", "", ignoreCase = true)
                    .replace(" kg", "")
                    .replace("Heinz -", "Heinz ")
                    .replace("Dairy Dale -", "Dairy Dale ")
                    .replace("Milk Nutri ", "Nutri ")
                    .replace("(", "")
                    .replace(")", "")

                val nameParts = name.split("-")

                name = nameParts[0]
                    .replace(Regex("\\s+"), " ")
                    .trim()

                product.variant = nameParts.getOrNull(1)
                    ?.replace(Regex("\\s+"), " ")
                    ?.trim()
                if (product.variant?.isBlank() == true)
                    product.variant = null

                product.name = name

                if (product.quantity == null) {
                    val details = veggieBoysService.getProductDetails(veggieBoysProduct.href!!)

                    if (details.perKg != null) {
                        product.saleType = SaleType.WEIGHT
                        product.weight = 1000
                    }
                }

                product.pricing = stores.map {
                    StorePricingInformation(
                        store = it.id,
                        price = if (veggieBoysProduct.onSpecial == null) veggieBoysProduct.price?.times(
                            100
                        )?.toInt() else null,
                        discountPrice = if (veggieBoysProduct.onSpecial != null) veggieBoysProduct.price?.times(
                            100
                        )?.toInt() else null,
                    )
                }.toMutableList()

                if (dairyDaleMultiBuy != null && product.id == "200") {
                    product.pricing?.forEach {
                        it.multiBuyQuantity = 2
                        it.multiBuyPrice = dairyDaleMultiBuy?.times(100)?.toInt()
                    }
                }

                product.image = "${baseUrl}${veggieBoysProduct.imagePath}"

                products.add(product)
            }
        }

        val retailer = Retailer("Veggie Boys", true, stores)

        return ScraperResult(retailer, products)
    }

}
