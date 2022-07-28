package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.RobertsonsMeatsApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*

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
                true
            )
        )

        val products = mutableListOf<RetailerProductInformation>()

        robertsonsMeatsService.getCategories().categories!!.forEach { robertsonsMeatsCategory ->
            robertsonsMeatsService.getProducts(robertsonsMeatsCategory.href!!).products?.forEach { robertsonsMeatsProduct ->
                val product = RetailerProductInformation(
                    retailer = retailerId,
                    id = robertsonsMeatsProduct.name,
                    saleType = if (robertsonsMeatsProduct.weight == null) SaleType.EACH else SaleType.WEIGHT,
                    weight = if (robertsonsMeatsProduct.weight != null) 1000 else null,
                    image = "${baseUrl}${robertsonsMeatsProduct.imagePath}"
                )

                var name = robertsonsMeatsProduct.name!!
                Units.all.forEach {
                    val result = extractAndRemoveQuantity(name, it)

                    name = result.first
                    if (result.second != null && robertsonsMeatsProduct.weight != null) {
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
                        robertsonsMeatsProduct.price!!.times(100).toInt()
                    )
                )

                products.add(product)
            }
        }

        val retailer = Retailer("Robertsons Meats", true, stores)

        return ScraperResult(retailer, products, retailerId)
    }

}