package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.WooComAPI
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*


abstract class WooCommerceScraper(
    private val id: String,
    private val retailer: Retailer,
    private val baseUrl: String,
) : Scraper() {
    var madButcherMap: Map<String, SaleType>? = null

    override suspend fun runScraper(): ScraperResult {
        val wooComService = generateJsonRequest(WooComAPI::class.java, baseUrl)

        val products = arrayListOf<RetailerProductInformation>()

        var page = 1
        //dummy value for first loop
        var lastPage = 1
        while (page <= lastPage) {
            //for each product in service
            wooComService.getProducts(page).filter { it.inStock }.forEach { wooComProduct ->

                //can be found from permalink
                val product = RetailerProductInformation(
                    retailer = id,
                    id = wooComProduct.id,
                    name = wooComProduct.name,
                    image = wooComProduct.images.firstOrNull()?.url,
                    saleType = SaleType.EACH
                )

                product.pricing = retailer.stores!!.map {
                    StorePricingInformation(
                        it.id,
                        wooComProduct.prices.price.toInt(),
                        discountPrice = if (wooComProduct.onSale) wooComProduct.prices.discountPrice.toInt() else null,
                        verified = true,
                    )
                }.toMutableList()

                val weightCandidate =
                    wooComProduct.attributes.firstOrNull { it.name == "Weight" }?.terms?.first()

                if (weightCandidate != null) {
                    val weightInGrams =
                        Units.GRAMS.regex.find(weightCandidate.name)?.groups?.get(1)?.value
                    val weightInKilograms =
                        Units.KILOGRAMS.regex.find(weightCandidate.name)?.groups?.get(1)?.value

                    if (weightInGrams != null) {
                        product.saleType = SaleType.WEIGHT
                        product.weight = weightInGrams.toDouble().toInt()
                    } else if (weightInKilograms != null) {
                        product.saleType = SaleType.WEIGHT
                        product.weight = weightInKilograms.toDouble().times(1000).toInt()
                    }
                }

                //replace ascii with "-"
                var name = wooComProduct.name.replace("&#8211;".toRegex(), "-")
                    .replace("&#8216;".toRegex(), "-")
                    .replace("&#8217;".toRegex(), "-")
                    .replace("&#038;".toRegex(), "-")
                    .replace("*", "")
                    .trim()

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

                val nameParts = name.split(" - ", limit = 2)

                name = nameParts[0]
                    .replace(Regex("\\s+"), " ")
                    .replace("()", "")
                    .trim()

                product.variant = nameParts.getOrNull(1)
                    ?.replace(Regex("\\s+"), " ")
                    ?.trim()
                if (product.variant?.isBlank() == true)
                    product.variant = null

                product.name = name

                if (product.weight == null && madButcherMap != null) {
                    product.saleType = madButcherMap!![wooComProduct.permaLink]

                    if (product.saleType == SaleType.WEIGHT)
                        product.weight = 1000
                }

                products.add(product)
            }
            page++
            if (wooComService.getProducts(page).isNotEmpty()) {
                lastPage++
            }
        }
        return ScraperResult(retailer, products)
    }
}