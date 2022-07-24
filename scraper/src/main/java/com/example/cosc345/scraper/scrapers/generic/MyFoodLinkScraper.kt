package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.MyFoodLinkApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.extensions.capitaliseNZ
import com.example.cosc345.shared.extensions.titleCase
import com.example.cosc345.shared.models.*
import kotlin.text.Typography.nbsp

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

                var page = 1
                // Dummy value for first loop
                var lastPage = 1

                while (page <= lastPage) {
                    val response = myFoodLinkHtmlService.getProducts(page)

                    response.lines!!.forEach { line ->

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

                            product.name = gtmData.name
                                .replace(Units.GRAMS.regex, "")
                                .replace(Units.KILOGRAMS.regex, "")
                                .replace(" Kg", "")
                                .lowercase()
                                .titleCase()
                                .capitaliseNZ()

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
                    }

                    if (response.pages != null)
                        lastPage = response.pages!!.toInt()
                    page++
                }
            }
        }

        retailer.stores = stores

        return ScraperResult(retailer, products)
    }
}