package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.FourSquareApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*

class FourSquareScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val baseUrl = "https://www.foursquare.co.nz"
        val fourSquareJsonService = generateJsonRequest(FourSquareApi::class.java, baseUrl)
        val fourSquareHtmlService = generateHtmlRequest(FourSquareApi::class.java, baseUrl)
        val retailerId = "foursquare"

        val stores = mutableListOf<Store>()
        val products = mutableListOf<RetailerProductInformation>()

        val storeWhitelist =
            arrayOf("Four Square Mitchells", "Four Square St Clair", "Four Square Port Chalmers")
        val fourSquareStores = fourSquareJsonService.getStores()

        fourSquareStores.forEach { fourSquareStore ->
            if (storeWhitelist.contains(fourSquareStore.name)) {
                val store = Store(
                    id = fourSquareStore.id,
                    name = fourSquareStore.name,
                    address = fourSquareStore.address,
                    latitude = fourSquareStore.latitude,
                    longitude = fourSquareStore.longitude,
                    automated = true
                )
                stores.add(store)
            }
        }

        val regionsStoreMap = fourSquareStores.groupBy { it.region }

        regionsStoreMap.forEach { regionMap ->
            val mailerId =
                fourSquareJsonService.getMailers(if (regionMap.key == "SI") "4316" else "3291")
                    .first().id

            val mailerProducts =
                fourSquareJsonService.getProductsForMailer(mailerId).values.toTypedArray().flatten()

            mailerProducts.forEach { fourSquareProduct ->
                if (fourSquareProduct.type == "product" && products.none { it.id == fourSquareProduct.name.trim() }) {
                    val product = RetailerProductInformation(
                        retailer = retailerId,
                        id = fourSquareProduct.name.trim(),
                        saleType = if (fourSquareProduct.saleType == "kg") SaleType.WEIGHT else SaleType.EACH,
                        weight = if (fourSquareProduct.saleType == "kg") 1000 else null,
                        image = fourSquareProduct.imageUrls.first()
                    )

                    var name = fourSquareProduct.name.trim()

                    Units.all.forEach {
                        val pair = extractAndRemoveQuantity(name, it)

                        if (pair.second != null) {
                            name = pair.first

                            when (it) {
                                Units.GRAMS -> {
                                    product.weight = pair.second?.toInt()
                                }
                                Units.KILOGRAMS -> {
                                    product.weight = pair.second?.times(1000)?.toInt()
                                }
                                else -> {
                                    product.weight = null
                                }
                            }

                            product.quantity =
                                "${product.quantity ?: ""} ${pair.second}${it}".trim()
                        }
                    }

                    name = name
                        .split(" excludes ", ignoreCase = true)[0]
                        .replace(" range", "", ignoreCase = true)
                        .replace(" loose", "", ignoreCase = true)
                        .replace(" varieties", "")
                        .replace(Regex("\\s+"), " ")
                        .trim()

                    product.name = name

                    product.pricing = regionMap.value.map {
                        StorePricingInformation(
                            it.id,
                            price = fourSquareProduct.price.toDouble().times(100).toInt(),
                            verified = true
                        )
                    }.toMutableList()

                    products.add(product)
                }
            }

            val specialsProducts =
                fourSquareHtmlService.getLocalSpecials("region_code=${regionMap.key};")

            specialsProducts.specials?.forEach { fourSquareProduct ->
                if (products.none { it.id == fourSquareProduct.name?.trim() }) {
                    val product = RetailerProductInformation(
                        retailer = retailerId,
                        id = fourSquareProduct.name?.trim(),
                        saleType = if (fourSquareProduct.saleType == "kg") SaleType.WEIGHT else SaleType.EACH,
                        weight = if (fourSquareProduct.saleType == "kg") 1000 else null,
                        image = "${baseUrl}${fourSquareProduct.imagePath}"
                    )

                    var name = fourSquareProduct.name!!.trim()

                    Units.all.forEach {
                        val pair = extractAndRemoveQuantity(name, it)

                        if (pair.second != null) {
                            name = pair.first

                            when (it) {
                                Units.GRAMS -> {
                                    product.weight = pair.second?.toInt()
                                }
                                Units.KILOGRAMS -> {
                                    product.weight = pair.second?.times(1000)?.toInt()
                                }
                                else -> {
                                    product.weight = null
                                }
                            }

                            product.quantity =
                                "${product.quantity ?: ""} ${pair.second}${it}".trim()
                        }
                    }

                    name = name
                        .split(" excludes ", ignoreCase = true)[0]
                        .replace(" range", "", ignoreCase = true)
                        .replace(" loose", "", ignoreCase = true)
                        .replace(" varieties", "")
                        .replace(Regex("\\s+"), " ")
                        .trim()

                    product.name = name

                    val price = "${fourSquareProduct.dollars}.${fourSquareProduct.cents}".toDouble()
                        .times(100).toInt()

                    product.pricing = regionMap.value.map {
                        StorePricingInformation(
                            it.id,
                            price = price,
                            verified = true
                        )
                    }.toMutableList()

                    products.add(product)
                }
            }
        }

        val retailer = Retailer("Four Square", true, stores)

        return ScraperResult(retailer, products)
    }
}