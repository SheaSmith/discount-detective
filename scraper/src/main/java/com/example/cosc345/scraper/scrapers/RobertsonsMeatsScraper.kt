package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.RobertsonsMeatsApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*

/**
 * The bespoke scraper for getting data from Robertsons Meats.
 *
 * # Process
 * The home page of Robertsons Meats is the first to be called, as the list of categories in the navigation can be scraped.
 *
 * Then each category is iterated, and the products for each is requested. This data is then scraped, processed and cleaned up.
 *
 * @constructor Create a new instance of this scraper.
 */
class RobertsonsMeatsScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val baseUrl = "https://www.robertsonsmeats.co.nz"
        val robertsonsMeatsService = generateHtmlRequest(RobertsonsMeatsApi::class.java, baseUrl)
        val retailerId = "robertsons-meats"

        val stores = listOf(
            Store(
                retailerId,
                "Robertsons Meats",
                "527 Hillside Road, Caversham, Dunedin 9012",
                -45.897719,
                170.4879171,
                true,
                Region.DUNEDIN
            )
        )

        val products = mutableListOf<RetailerProductInformation>()

        robertsonsMeatsService.getCategories().categories!!.forEach { (href) ->
            robertsonsMeatsService.getProducts(href!!).products?.forEach { (productName, imagePath, price, weight) ->
                val product = RetailerProductInformation(
                    retailer = retailerId,
                    id = productName,
                    saleType = if (weight == null) SaleType.EACH else SaleType.WEIGHT,
                    weight = if (weight != null) 1000 else null,
                    image = "${baseUrl}${imagePath}",
                    automated = true,
                    verified = false
                )

                var name = productName!!
                Units.all.forEach {
                    val result = extractAndRemoveQuantity(name, it)

                    name = result.first
                    if (result.second != null && weight != null) {
                        product.quantity = "${result.second}${it}"

                        if (it == Units.GRAMS) {
                            product.weight = result.second?.toInt()
                        } else if (it == Units.KILOGRAMS) {
                            product.weight = result.second?.times(1000)?.toInt()
                        }
                    }
                }

                product.name = name

                product.pricing = mutableListOf(
                    StorePricingInformation(
                        retailerId,
                        price!!.times(100).toInt(),
                        automated = true,
                        verified = false
                    )
                )

                products.add(product)
            }
        }

        val retailer = Retailer(
            name = "Robertsons Meats",
            automated = true,
            verified = false,
            stores = stores,
            colourLight = 0xFFd2e4ff,
            onColourLight = 0xFF001c37,
            colourDark = 0xFF00487f,
            onColourDark = 0xFFd2e4ff,
            initialism = "RM",
            local = true
        )

        return ScraperResult(retailer, products, retailerId)
    }

}