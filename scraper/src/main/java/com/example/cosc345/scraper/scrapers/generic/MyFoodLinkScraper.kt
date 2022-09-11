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
 * @param storeWhiteList The stores that we can query as they we are currently geolocked, with the key being the store ID and the value being the region.
 *
 * @constructor Create a new instance of this scraper, for the retailer specified in the constructor.
 */
abstract class MyFoodLinkScraper(
    private val id: String,
    private val retailer: Retailer,
    private val baseUrl: String,
    private val storeWhiteList: Map<String, String>
) : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val myFoodLinkJsonService = generateJsonRequest(MyFoodLinkApi::class.java, baseUrl)

        val stores = mutableListOf<Store>()
        val products = mutableListOf<RetailerProductInformation>()

        myFoodLinkJsonService.getStores()
            .forEach { (hostname, myFoodLinkName, myFoodLinkId, type) ->
                if (myFoodLinkName != null && type == "ecommerce"
                    && storeWhiteList.contains(myFoodLinkName)
                ) {
                    stores.add(
                        Store(
                            id = myFoodLinkId,
                            name = myFoodLinkName.replace(retailer.name!!, "").trim(),
                            automated = true,
                            region = storeWhiteList[myFoodLinkName]
                        )
                    )

                    val myFoodLinkHtmlService = generateHtmlRequest(
                        MyFoodLinkApi::class.java,
                        "https://${hostname}"
                    )

                    var page = 1
                    // Dummy value for first loop
                    var lastPage = 1

                    while (page <= lastPage) {
                        val response = myFoodLinkHtmlService.getProducts(page)

                        if (response.gtmData == null) {
                            delay(1000)
                            continue
                        }

                        response.lines!!.forEach { (htmlName, price, _, _, savingsDollars, savingsCents, multiBuyQuantity, multiBuyDollars, multiBuyCents, image) ->

                            try {
                                val gtmData =
                                    response.gtmData!!.first { it.eventType == "productListImpression" }.ecommerce.impressions!!.first {
                                        it.name
                                            .trim()
                                            .replace(Regex("\\s+"), " ")
                                            .replace(nbsp.toString(), " ")
                                            .equals(
                                                htmlName!!
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
                                        image = image,
                                        automated = true,
                                        verified = false
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

                                    if (product.brandName != null) {
                                        product.name =
                                            product.name!!.replace(product.brandName!!, "").trim()

                                        if (product.name.isNullOrBlank()) {
                                            product.name = product.brandName
                                            product.brandName = null
                                        }
                                    }

                                    products.add(product)
                                }

                                product = products.first { it.id == gtmData.id }

                                if (product.pricing?.none { it.store == myFoodLinkId } != false) {
                                    if (product.pricing == null)
                                        product.pricing = mutableListOf()

                                    val pricing = StorePricingInformation(
                                        store = myFoodLinkId,
                                        automated = true,
                                        verified = false
                                    )

                                    val centsRegex = Regex("(\\d+)c")
                                    val dollarsRegex = Regex("\\\$([\\d.]+)")


                                    val discount =
                                        savingsDollars?.let {
                                            dollarsRegex.find(it)?.groups?.get(
                                                1
                                            )?.value?.toDouble()
                                        }
                                            ?: savingsCents?.let {
                                                centsRegex.find(it)?.groups?.get(1)?.value?.toDouble()
                                                    ?.div(100)
                                            }

                                    if (discount != null) {
                                        pricing.discountPrice = (price!! * 100).toInt()
                                        pricing.price = ((price + discount) * 100).toInt()
                                    } else {
                                        pricing.price = (price!! * 100).toInt()
                                    }

                                    if (multiBuyQuantity != null && (multiBuyDollars != null || multiBuyCents != null)) {
                                        pricing.multiBuyQuantity = multiBuyQuantity.toInt()

                                        pricing.multiBuyPrice =
                                            multiBuyDollars?.toDouble()?.times(100)?.toInt()
                                                ?: multiBuyCents?.toInt()
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