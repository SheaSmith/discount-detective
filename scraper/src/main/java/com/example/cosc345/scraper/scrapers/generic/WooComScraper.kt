package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.MyFoodLinkApi
import com.example.cosc345.scraper.api.WooComAPI
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.models.shopify.ShopifyProduct
import com.example.cosc345.shared.models.*
import okhttp3.Call
import okhttp3.ResponseBody
import retrofit2.Retrofit


abstract class WooComScraper (
    private val id: String,
    private val retailer: Retailer,
    private val baseUrl: String,
) : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val wooComService = generateJsonRequest(WooComAPI::class.java, baseUrl)

        val products = arrayListOf<RetailerProductInformation>()

        var page = 1
        //dummy value for first loop
        var lastPage = 1
        while (page <= lastPage) {
            //for each product in service
            wooComService.getProducts(page).forEach { wooComProduct ->

                //can be found from permalink
                val product = RetailerProductInformation(
                    id = wooComProduct.id,
                    name = wooComProduct.name,
                    saleType = if (wooComProduct.type == "simple") SaleType.EACH else SaleType.WEIGHT
                )
                //adjust units for price
                val unit_conv = (100 / Math.pow(
                    10.0,
                    wooComProduct.prices.currency_minor_unit.toDouble()
                )).toInt()
                product.pricing = arrayListOf(
                    StorePricingInformation(
                        id,
                        wooComProduct.prices.price.toInt() * unit_conv,
                        verified = true,
                    )

                )

                //weight
                // TODO: ONLY DEALING WITH ONE WEIGHT (ie might be 2kg and 4kg variants)
                wooComProduct.variants.forEach { variant ->
                    variant.attributes.forEach { attribute ->
                        if (attribute.id == "Weight") {
                            val match =
                                Regex("([\\d.]+)([\\w]+)").find(attribute.value.toString())!!
                            val (number, unit) = match.destructured
                            var numericalVal = number.toInt()
                            if (unit.lowercase() == "kg") {
                                numericalVal *= 1000
                            }
                            product.weight = numericalVal
                        }
                    }
                }
                //check if weight in  title
                var search = wooComProduct.name.toString().lowercase()
                search = search.replace("\\s".toRegex(), "") //remove whitespace
                val matchkg = Regex("\\dkg").containsMatchIn(search)
                //grams anywhere
                val matchg = Regex("\\dg").containsMatchIn(search)
                if (matchkg or matchg) {
                    val match = Regex("([0\\d.]+)([\\w]+)").find(search)!!
                    val (number, unit) = match.destructured
                    var numericalVal = number.toDouble()
                    if (unit.lowercase() == "kg") {
                        numericalVal *= 1000
                    }
                    product.weight = numericalVal.toInt()
                    product.saleType = SaleType.WEIGHT
                }
                products.add(product)
            }
            page++
            if (wooComService.getProducts(page).isNotEmpty()){
                lastPage++
            }
        }
        return ScraperResult(retailer, products)
    }

}