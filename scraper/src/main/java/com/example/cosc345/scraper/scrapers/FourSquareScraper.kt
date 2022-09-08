package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.FourSquareApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*

/**
 * The bespoke scraper for getting data from Four Square, specifically the specials and products in the mailer.
 *
 * # Process
 * Firstly, the scraper gets a list of all stores from the Four Square API. Each store is then grouped by its region code, which is either SI for South Island, LNI for Lower North Island, or UNI for Upper North Island.
 *
 * For each region, the mailer is then requested. Firstly, a list of issues is requested, with the ID being taken from the latest mailer issue. Then the clickable elements of mailer page are requested, and then parsed into products.
 *
 * Then the local specials page for each region is scraped. This data is cleaned up in a similar way to the mailer data.
 *
 * @constructor Create a new instance of this scraper.
 */
class FourSquareScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val retailerName = "Four Square"
        val baseUrl = "https://www.foursquare.co.nz"
        val fourSquareJsonService = generateJsonRequest(FourSquareApi::class.java, baseUrl)
        val fourSquareHtmlService = generateHtmlRequest(FourSquareApi::class.java, baseUrl)
        val retailerId = "foursquare"

        val stores = mutableListOf<Store>()
        val products = mutableListOf<RetailerProductInformation>()

        val storeWhitelist = mapOf(
            "Four Square Mitchells" to Region.DUNEDIN,
            "Four Square St Clair" to Region.DUNEDIN,
            "Four Square Port Chalmers" to Region.DUNEDIN,
            "Four Square Ascot" to Region.INVERCARGILL,
            "Four Square Newfield" to Region.INVERCARGILL,
            "Four Square Bluff" to Region.INVERCARGILL,
            "Four Square Otatara" to Region.INVERCARGILL,
            "Four Square Buffalo Beach" to Region.WHITIANGA,
            "Four Square Matarangi" to Region.WHITIANGA,
            "Four Square Tairua" to Region.WHITIANGA
        )
        val fourSquareStores = fourSquareJsonService.getStores()

        fourSquareStores.forEach { fourSquareStore ->
            if (storeWhitelist.contains(fourSquareStore.name)) {
                val store = Store(
                    id = fourSquareStore.id,
                    name = fourSquareStore.name.replace(retailerName, "").trim(),
                    address = fourSquareStore.address,
                    latitude = fourSquareStore.latitude,
                    longitude = fourSquareStore.longitude,
                    automated = true,
                    region = storeWhitelist[fourSquareStore.name]
                )
                stores.add(store)
            }
        }

        val regionsStoreMap = fourSquareStores.groupBy { it.region }

        regionsStoreMap.forEach { regionMap ->
            val mailerId =
                fourSquareJsonService.getMailers(if (regionMap.key == "SI") "4316" else "3291")
                    .first().id

            val regionStores = stores.filter { store -> regionMap.value.any { it.id == store.id } }

            if (regionStores.isNotEmpty()) {

                val mailerProducts =
                    fourSquareJsonService.getProductsForMailer(mailerId).values.toTypedArray()
                        .flatten()

                mailerProducts.forEach { fourSquareProduct ->
                    if (fourSquareProduct.type == "product" && products.none { it.id == fourSquareProduct.name.trim() }) {
                        val product = RetailerProductInformation(
                            retailer = retailerId,
                            id = fourSquareProduct.name.trim(),
                            saleType = if (fourSquareProduct.saleType == "kg") SaleType.WEIGHT else SaleType.EACH,
                            weight = if (fourSquareProduct.saleType == "kg") 1000 else null,
                            image = fourSquareProduct.imageUrls.first(),
                            automated = true,
                            verified = false
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

                        product.pricing = regionStores.map {
                            StorePricingInformation(
                                it.id,
                                price = fourSquareProduct.price.toDouble().times(100).toInt(),
                                automated = true,
                                verified = false
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
                            image = "${baseUrl}${fourSquareProduct.imagePath}",
                            automated = true,
                            verified = false
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

                        val price =
                            "${fourSquareProduct.dollars}.${fourSquareProduct.cents}".toDouble()
                                .times(100).toInt()

                        product.pricing = regionStores.map {
                            StorePricingInformation(
                                it.id,
                                price = price,
                                automated = true,
                                verified = false
                            )
                        }.toMutableList()

                        products.add(product)
                    }
                }
            }
        }

        val retailer = Retailer(
            name = retailerName,
            automated = true,
            verified = false,
            stores = stores,
            colourLight = 0xFF9bf7a8,
            onColourLight = 0xFF002109,
            colourDark = 0xFF005322,
            onColourDark = 0xFF9bf7a8,
            initialism = "FS",
            local = true
        )

        return ScraperResult(retailer, products, retailerId)
    }
}