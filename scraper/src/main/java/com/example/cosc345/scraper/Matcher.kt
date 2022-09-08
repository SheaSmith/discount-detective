package com.example.cosc345.scraper

import com.example.cosc345.scraper.models.MatcherGrouping
import com.example.cosc345.scraper.scrapers.*
import com.example.cosc345.scraper.scrapers.foodstuffs.NewWorldScraper
import com.example.cosc345.scraper.scrapers.foodstuffs.PakNSaveScraper
import com.example.cosc345.scraper.scrapers.myfoodlink.FreshChoiceScraper
import com.example.cosc345.scraper.scrapers.myfoodlink.SuperValueScraper
import com.example.cosc345.scraper.scrapers.shopify.LeckiesButcheryScraper
import com.example.cosc345.scraper.scrapers.shopify.PrincesStreetButcherScraper
import com.example.cosc345.scraper.scrapers.shopify.YogijisFoodMartScraper
import com.example.cosc345.scraper.scrapers.wixstores.SpeltBakeryScraper
import com.example.cosc345.scraper.scrapers.woocommerce.*
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * The matcher is responsible for running all of the scrapers, and then merging the products together.
 */
@OptIn(ExperimentalTime::class)
class Matcher {
    /**
     * Run all scrapers.
     *
     * @return A pair, with the first item being the retailers map (key: ID, value: retailer) and the second item being the list of retailer product info.
     */
    suspend fun runScrapers(): Pair<Map<String, Retailer>, List<RetailerProductInformation>> {
        val scrapers = setOf(
            CountdownScraper(),
            NewWorldScraper(),
            PakNSaveScraper(),
            FreshChoiceScraper(),
            SuperValueScraper(),
            LeckiesButcheryScraper(),
            PrincesStreetButcherScraper(),
            YogijisFoodMartScraper(),
            SpeltBakeryScraper(),
            CouplandsScraper(),
            DeepCreekDeliScraper(),
            HarbourFishScraper(),
            MadButcherScraper(),
            OriginFoodScraper(),
            TasteNatureScraper(),
            FourSquareScraper(),
            RobertsonsMeatsScraper(),
            VeggieBoysScraper(),
            WarehouseScraper()
        )

        val retailers = mutableMapOf<String, Retailer>()
        val retailerProductInfo = mutableListOf<RetailerProductInformation>()

        scrapers.forEach {
            val time = measureTime {
                val result = it.runScraper()

                retailerProductInfo.addAll(result.productInformation)
                retailers[result.retailerId] = result.retailer
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

        return Pair(retailers, retailerProductInfo)
    }

    /**
     * Merge products based on their barcode.
     *
     * # Process
     * All products with matching barcodes are merged, even if there is only a two-degree match (i.e. a set of products with the barcodes (1234), (1234, 5678) and (5678) will be merged.
     *
     * However, the exception to this is that each product cannot have more than one product from the same retailer as a match. So they will be split off into their own products.
     *
     * @param retailerProductInfo The product information that should be merged.
     * @param retailers The retailer to return back.
     * @return A pair with the retailers being the first item (key: ID, value: retailer), and the list of product as the second item (key: product ID, value: product).
     */
    fun matchBarcodes(
        retailerProductInfo: MutableList<RetailerProductInformation>,
        retailers: Map<String, Retailer>
    ): Pair<Map<String, Retailer>, Map<String, Product>> {
        val products = mutableListOf<Product>()
        val time = measureTime {
            val infoWithBarcodes = retailerProductInfo.filter { it.barcodes?.isNotEmpty() == true }
            retailerProductInfo.removeAll(infoWithBarcodes.toSet())

            val infoGroupedByBarcode =
                infoWithBarcodes.flatMap { product -> product.barcodes!!.map { it to product } }
                    .groupBy({ it.first }, { it.second }).mapValues { it.value.toMutableList() }
                    .toMutableMap()

            val skipBarcodes = mutableListOf<String>()

            infoGroupedByBarcode.forEach { pair ->
                if (pair.key !in skipBarcodes) {
                    val barcodes = pair.value.flatMap { it.barcodes!! }.distinct().toMutableList()
                    val allRetailers = pair.value.map { it.retailer }.toSet()
                    barcodes.remove(pair.key)

                    val productsInOtherBarcodes =
                        infoGroupedByBarcode.filter {
                            it.key in barcodes
                        }.values.flatten()
                            .distinct()
                            .filter { it !in pair.value && it.retailer !in allRetailers }
                    pair.value.addAll(productsInOtherBarcodes)

                    skipBarcodes.addAll(productsInOtherBarcodes.flatMap { it.barcodes!! }
                        .distinct())
                }
            }

            skipBarcodes.forEach { infoGroupedByBarcode.remove(it) }

            products.addAll(infoGroupedByBarcode.values.map { Product(it) })
            products.addAll(retailerProductInfo.map { Product(mutableListOf(it)) })

            products.forEach { product ->
                product.information!!.removeAll { info ->
                    products.flatMap { it.information!! }
                        .count { it.id == info.id && it.retailer == info.retailer } > 1
                }
            }
            products.removeAll { it.information.isNullOrEmpty() }
        }

        printStatus(products, retailers)
        print("Barcode matching took ${time.toString(DurationUnit.SECONDS, 1)}")

        val mappedProducts = mapProducts(products)

        return Pair(retailers, mappedProducts)
    }

    /**
     * Merge products based on their names (including brand name and variant).
     *
     * # Process
     * Essentially each value (name, brand name and variant) is stripped of plurals and symbols, split into the individual words and they are checked to see if they match.
     *
     * The variant is an optional match, but the brand name and name must match.
     *
     * Some "generic", store-name brands are excluded from the match (so Pams xyz will match Countdown xyz).
     *
     * Additionally, both products must have the same sale type, and the same quantity.
     *
     * @param productMap The product information that should be merged.
     * @param retailers The retailer to return back.
     * @return A pair with the retailers being the first item (key: ID, value: retailer), and the list of product as the second item (key: product ID, value: product).
     */
    fun matchNames(
        productMap: Map<String, Product>,
        retailers: Map<String, Retailer>
    ): Pair<Map<String, Retailer>, Map<String, Product>> {
        val newProductsMap = mutableMapOf<String, Product>()

        val time = measureTime {
            val test =
                productMap
                    .flatMap { product ->
                        product.value.information!!
                            .map { it to product.key }
                    }
                    .groupBy { MatcherGrouping(it.first) }
                    .mapValues { it.value.toMutableList() }
                    .toList()
                    .toMutableList()

            val retailersFix = test.filter { map ->
                map.second.map { it.first.retailer }.distinct().size != map.second.size
            }

            retailersFix.forEach { entry ->
                val retailersForEntry = entry.second.map { it.first.retailer }.distinct()

                retailersForEntry.forEach { retailer ->
                    val infoForRetailer = entry.second.filter { it.first.retailer == retailer }

                    if (infoForRetailer.size > 1) {
                        val toRemove =
                            infoForRetailer.filterIndexed { index, _ -> index != 0 }.toSet()
                        test.first { it.first == entry.first }.second.removeAll(toRemove)

                        toRemove.forEach {
                            test.add(Pair(MatcherGrouping(it.first), mutableListOf(it)))
                        }
                    }
                }
            }

            val newProductIdMap = mutableMapOf<String, String>()

            test.map { it.second }.forEach { mapList ->
                val productIds = mapList.map { newProductIdMap[it.second] ?: it.second }.toSet()

                val matchingProductIds = newProductsMap.keys.intersect(productIds)

                if (matchingProductIds.isNotEmpty()) {
                    val ids = matchingProductIds.filterIndexed { index, _ -> index != 0 }
                    val newId = matchingProductIds.first()

                    val productInfoToAdd = mapList.map { it.first }.toMutableList()
                    if (ids.isNotEmpty()) {
                        ids.forEach {
                            productInfoToAdd.addAll(newProductsMap[it]!!.information!!)
                            newProductsMap.remove(it)
                            newProductIdMap[it] = newId
                        }
                    }

                    mapList.map { it.second }.forEach {
                        if (!newProductIdMap.containsKey(it)) {
                            newProductIdMap[it] = newId
                        }
                    }

                    newProductsMap[newId]!!.information!!.addAll(productInfoToAdd)
                } else {
                    val newId = mapList.first().second

                    val otherIds =
                        mapList.filterIndexed { index, _ -> index != 0 }.map { it.second }
                    otherIds.forEach {
                        newProductIdMap[it] = newId
                    }

                    val product = Product(mapList.map { it.first }.toMutableList())
                    newProductsMap[newId] = product
                }
            }

            newProductsMap.forEach { product ->
                product.value.information!!.removeAll { info ->
                    newProductsMap.values.flatMap { it.information!! }
                        .count { it.id == info.id && it.retailer == info.retailer } > 1
                }
            }
            newProductsMap.values.removeAll { it.information.isNullOrEmpty() }
        }

        printStatus(newProductsMap.values.toList(), retailers)
        print("Barcode matching took ${time.toString(DurationUnit.SECONDS, 1)}")

        return Pair(retailers, newProductsMap)
    }

    private fun mapProducts(products: List<Product>): Map<String, Product> {
        return products.associateBy { product ->
            val barcodes =
                product.information!!.filter { it.barcodes != null }.flatMap { it.barcodes!! }

            if (barcodes.isNotEmpty()) {
                val barcodeUsage = barcodes.groupingBy { it }.eachCount()

                barcodeUsage.maxBy { it.value }.key
            } else {
                val info = product.information!!.first()
                "${info.id!!.replace(Regex("[/.#$\\[\\]]"), "")}-${info.retailer!!}"
            }
        }
    }

    private fun printStatus(products: List<Product>, retailers: Map<String, Retailer>) {
        println("Number of retailer items per product")
        val count = products.groupBy { it.information!!.size }
        count.forEach {
            println("${it.key}: ${it.value.size}")
        }

        val groceryProducts =
            products.filter { product -> product.information!!.all { it.saleType == SaleType.EACH } }
        println("${
            groceryProducts.count { it.information!!.size > 1 }
                .toDouble() / groceryProducts.size.toDouble() * 100
        }% of products sold by unit matched.")

        retailers.forEach { retailer ->
            val total =
                products.count { product -> product.information?.any { it.retailer == retailer.key } == true }
            val totalMatched =
                products.count { product -> product.information?.any { it.retailer == retailer.key } == true && product.information?.size!! > 1 }

            println("For ${retailer.value.name} ${totalMatched.toDouble() / total.toDouble() * 100}% matched.")
        }

        println(
            "${
                products.count { it.information!!.size > 1 }
                    .toDouble() / products.size.toDouble() * 100
            }% of products matched."
        )
    }
}