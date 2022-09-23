package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.VeggieBoysApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*

/**
 * The scraper for the bespoke Veggie Boys store.
 *
 * # Process
 * A list of all Veggie Boys products is requested. Then each product is scraped, processed and cleaned up. For some products, it is necessary to determine whether it is sold per KG or not, so a specific request to the product details page is required.
 *
 * @constructor Create a new instance of this scraper.
 */
class VeggieBoysScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val baseUrl = "https://veggieboys.co.nz"
        val veggieBoysService = generateHtmlRequest(VeggieBoysApi::class.java, baseUrl)
        val retailerId = "veggie-boys"

        val stores = listOf(
            Store(
                "${retailerId}-south",
                "South Dunedin",
                "16 Prince Albert Road, St Kilda, Dunedin 9012",
                -45.90009,
                170.503682,
                true,
                Region.DUNEDIN
            ),
            Store(
                "${retailerId}-north",
                "North Dunedin",
                "16 Prince Albert Road, St Kilda, Dunedin 9012",
                -45.867877,
                170.514351,
                true,
                Region.DUNEDIN
            )
        )

        val products = mutableListOf<RetailerProductInformation>()

        var dairyDaleMultiBuy: Double? = null

        veggieBoysService.getProducts().products!!.forEach { (productName, price, imagePath, id, href, onSpecial) ->
            if (id == "275") {
                dairyDaleMultiBuy = price
            } else if (productName?.isNotEmpty() == true) {

                val product = RetailerProductInformation(
                    retailer = retailerId,
                    id = id,
                    saleType = SaleType.EACH,
                    automated = true,
                    verified = false
                )

                var name: String = productName
                Units.all.forEach {
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

                name = name
                    .replace("loose", "", ignoreCase = true)
                    .replace("Biscuits - ", "", ignoreCase = true)
                    .replace("Cream - ", "", ignoreCase = true)
                    .replace(" kg", "")
                    .replace("Heinz -", "Heinz ")
                    .replace("Dairy Dale -", "Dairy Dale Milk ")
                    .replace("Milk Nutri ", "Nutri ")
                    .replace("(", "")
                    .replace(")", "")

                val nameParts = name.split("-", limit = 1)

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
                    val details = veggieBoysService.getProductDetails(href!!)

                    if (details.perKg?.isNotEmpty() == true) {
                        product.saleType = SaleType.WEIGHT
                        product.weight = 1000
                    }
                }

                product.pricing = stores.map {
                    StorePricingInformation(
                        store = it.id,
                        price = if (onSpecial == null) price?.times(
                            100
                        )?.toInt() else null,
                        discountPrice = if (onSpecial != null) price?.times(
                            100
                        )?.toInt() else null,
                        automated = true,
                        verified = false
                    )
                }.toMutableList()

                if (dairyDaleMultiBuy != null && product.id == "200") {
                    product.pricing?.forEach {
                        it.multiBuyQuantity = 2
                        it.multiBuyPrice = dairyDaleMultiBuy?.times(100)?.toInt()
                    }
                }

                product.image = "${baseUrl}${imagePath}"

                products.add(product)
            }
        }

        val retailer = Retailer(
            name = "Veggie Boys",
            automated = true,
            verified = false,
            stores = stores,
            colourLight = 0xFFaaf5a4,
            onColourLight = 0xFF002204,
            colourDark = 0xFF045316,
            onColourDark = 0xFFaaf5a4,
            initialism = "VB",
            local = true
        )

        return ScraperResult(retailer, products, retailerId)
    }

}
