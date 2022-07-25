package com.example.cosc345.scraper

import com.example.cosc345.scraper.models.MatcherGrouping
import com.example.cosc345.scraper.scrapers.CountdownScraper
import com.example.cosc345.scraper.scrapers.foodstuffs.PakNSaveScraper
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class Matcher {
    suspend fun run() {
        val scrapers = arrayOf(
            CountdownScraper(),
            //NewWorldScraper(),
            PakNSaveScraper(),
            //FreshChoiceScraper(),
            //SuperValueScraper()
        )

        val retailers = mutableListOf<Retailer>()
        val retailerProductInfo = mutableListOf<RetailerProductInformation>()

        scrapers.forEach {
            val time = measureTime {
                val result = it.runScraper()

                retailerProductInfo.addAll(result.productInformation)
                retailers.add(result.retailer)
            }

            println(
                "Ran scraper ${it.javaClass.simpleName} in ${
                    time.toString(
                        DurationUnit.SECONDS,
                        1
                    )
                } seconds."
            )
        }

        val products: MutableList<Product>
        val time = measureTime {
            val infoWithBarcodes = retailerProductInfo.filter { it.barcodes != null }
            val allBarcodes = infoWithBarcodes.flatMap { it.barcodes!! }.distinct()

            val productInfoByBarcode =
                allBarcodes.associateWith { barcode ->
                    infoWithBarcodes.filter { barcode in it.barcodes!! }.toSet()
                }.values.toMutableList()

            products = mutableListOf()
            val iterator = productInfoByBarcode.iterator()
            val itemsToRemove = mutableListOf<Set<RetailerProductInformation>>()
            iterator.forEach { productList ->
                if (productList in itemsToRemove) {
                    iterator.remove()
                } else {
                    val matchingProducts = productInfoByBarcode.filter { productList2 ->
                        productList2.intersect(productList)
                            .isNotEmpty() && productList2.none { info -> productList.any { it.retailer == info.retailer && info.id != it.id } }
                    }

                    val infoList = matchingProducts.flatten().distinct().toMutableList()
                    products.add(Product(infoList))
                    itemsToRemove.addAll(matchingProducts)
                    retailerProductInfo.removeAll(infoList)
                    iterator.remove()
                }
            }

            products.addAll(retailerProductInfo.map { Product(mutableListOf(it)) })

            val byInfo = products.flatMap { product ->
                product.information!!.map { info ->
                    product to MatcherGrouping(info)
                }
            }
                .associateBy({ (_, info) -> info }, valueTransform = { (product, _) -> product })

            val newProducts = mutableListOf<Product>()
            val comparisionInfos = byInfo.toMutableMap()
            byInfo.forEach { map ->
                val match = comparisionInfos.filterKeys { info ->
                    map.key == info
                }.values.firstOrNull()

                if (match != null) {
                    match.information!!.addAll(map.value.information!!)
                } else {
                    newProducts.add(map.value)
                }

                comparisionInfos.remove(map.key)
            }
        }

        println(
            "${
                products.count { it.information!!.size > 1 }
                    .toDouble() / products.size.toDouble() * 100
            }% of products matched. Matching took ${
                time.toString(
                    DurationUnit.SECONDS,
                    1
                )
            }."
        )
    }
}