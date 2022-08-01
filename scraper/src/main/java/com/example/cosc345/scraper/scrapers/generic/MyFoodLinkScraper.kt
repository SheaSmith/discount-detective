package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.MyFoodLinkApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.extensions.capitaliseNZ
import com.example.cosc345.shared.extensions.titleCase
import com.example.cosc345.shared.models.*
import kotlinx.coroutines.delay
import kotlin.text.Typography.nbsp

/**
 * A generic scraper that scrapes from MyFoodLink based stores (for example, FreshChoice and SuperValue).
 *
 * # Process
 * Firstly, all of the stores for the particular retailer are queried. At least for FreshChoice and SuperValue, each store has its own dedicated website. Only websites of the type "ecommerce" are looked at, as the others do not have online shopping available.
 *
 * Each store is then iterated, and for each store we essentially do a search request without any filters and then iterate through each page. A combination of HTML and JSON parsing is used to try and find as much data as we can.
 *
 * The name of the product has significant cleaning done to it, to try and make sure it is consistent with the other supermarkets with more structured naming fields.
 *
 * @param id The ID of the retailer we want to use.
 * @param retailer The retailer object to attach stores to.
 * @param baseUrl The base URL for the get stores request.
 * @param storeWhiteList The stores that we can query as they we are currently geolocked to Dunedin.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper, for the retailer specified in the constructor.
 */
abstract class MyFoodLinkScraper(
    private val id: String,
    private val retailer: Retailer,
    private val baseUrl: String,
    private val storeWhiteList: Array<String>
) : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val myFoodLinkJsonService = generateJsonRequest(MyFoodLinkApi::class.java, baseUrl)

        val stores = mutableListOf<Store>()
        val products = mutableListOf<RetailerProductInformation>()

        myFoodLinkJsonService.getStores().forEach { myFoodLinkStore ->
            if (myFoodLinkStore.name != null && myFoodLinkStore.type == "ecommerce"
                && storeWhiteList.contains(myFoodLinkStore.name)
            ) {
                stores.add(
                    Store(
                        id = myFoodLinkStore.id,
                        name = myFoodLinkStore.name,
                        automated = true
                    )
                )

                val myFoodLinkHtmlService = generateHtmlRequest(
                    MyFoodLinkApi::class.java,
                    "https://${myFoodLinkStore.hostname}"
                )

                var page = 276
                // Dummy value for first loop
                var lastPage = 276

                while (page <= lastPage) {
                    val response = myFoodLinkHtmlService.getProducts(page)

                    if (response.gtmData == null) {
                        delay(1000)
                        continue
                    }

                    response.lines!!.forEach { line ->

                        try {
                            val gtmData =
                                response.gtmData!!.first { it.eventType == "productListImpression" }.ecommerce.impressions!!.first {
                                    it.name
                                        .trim()
                                        .replace(Regex("\\s+"), " ")
                                        .replace(nbsp.toString(), " ")
                                        .equals(
                                            line.name!!
                                                .trim()
                                                .replace(Regex("\\s+"), " ")
                                                .replace(nbsp.toString(), " "),
                                            true
                                        )
                                }


                            var product = products.firstOrNull { it.id == gtmData.id }

                            if (product == null) {
                                product = RetailerProductInformation(
                                    retailer = id,
                                    id = gtmData.id,
                                    brandName = gtmData.brand,
                                    saleType = if (gtmData.saleType == "kg") SaleType.WEIGHT else SaleType.EACH,
                                    weight = if (gtmData.saleType == "kg") 1000 else null,
                                    barcodes = listOf(gtmData.id),
                                    image = line.image
                                )

                                val weightGrams =
                                    Units.GRAMS.regex.find(gtmData.name)?.groups
                                        ?.get(1)?.value?.toDouble()

                                val weightKilograms =
                                Units.GRAMS.regex.find(gtmData.name)?.groups
                                    ?.get(1)?.value?.toDouble()

                            if (product.weight == null) {
                                product.weight = weightGrams?.toInt()
                                    ?: weightKilograms?.times(1000)?.toInt()
                            }

                            product.quantity = if (weightGrams != null) {
                                "${weightGrams}${Units.GRAMS}"
                            } else if (weightKilograms != null) {
                                "${weightKilograms}${Units.KILOGRAMS}"
                            } else {
                                null
                            }

                            var name = gtmData.name
                            // Strip out the weight from the title if it still exists
                            Units.all.forEach {
                                name = name
                                    .replace(it.regex, "")
                            }

                            product.name = name
                                .replace(" Kg", "", ignoreCase = true)
                                .lowercase()
                                .titleCase()
                                .capitaliseNZ()
                                .replace(Regex("\\s+"), " ")
                                .trim()

                            if (product.saleType == SaleType.WEIGHT) {
                                product.name =
                                    product.name!!.replace(" Loose", "", ignoreCase = true)
                            }

                            products.add(product)
                        }

                        product = products.first { it.id == gtmData.id }

                        if (product.pricing?.none { it.store == myFoodLinkStore.id } != false) {
                            if (product.pricing == null)
                                product.pricing = mutableListOf()

                            val pricing = StorePricingInformation(
                                store = myFoodLinkStore.id,
                                verified = true,
                            )

                            val centsRegex = Regex("(\\d+)c")
                            val dollarsRegex = Regex("\\\$([\\d.]+)")


                            val discount =
                                line.savingsDollars?.let { dollarsRegex.find(it)?.groups?.get(1)?.value?.toDouble() }
                                    ?: line.savingsCents?.let {
                                        centsRegex.find(it)?.groups?.get(1)?.value?.toDouble()
                                            ?.div(100)
                                    }

                            if (discount != null) {
                                pricing.discountPrice = (line.price!! * 100).toInt()
                                pricing.price = ((line.price!! + discount) * 100).toInt()
                            } else {
                                pricing.price = (line.price!! * 100).toInt()
                            }

                            if (line.multiBuyQuantity != null && (line.multiBuyDollars != null || line.multiBuyCents != null)) {
                                pricing.multiBuyQuantity = line.multiBuyQuantity?.toInt()

                                pricing.multiBuyPrice =
                                    line.multiBuyDollars?.toDouble()?.times(100)?.toInt()
                                        ?: line.multiBuyCents?.toInt()
                            }

                            product.pricing?.add(pricing)
                        }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    if (response.pages != null)
                        lastPage = response.pages!!.toInt()
                    page++
                }
            }
        }

        retailer.stores = stores

        return ScraperResult(retailer, products, id)
    }
}