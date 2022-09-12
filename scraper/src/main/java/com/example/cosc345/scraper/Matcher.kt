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

            infoGroupedByBarcode.forEach { (key, value) ->
                if (key !in skipBarcodes) {
                    val barcodes = value.flatMap { it.barcodes!! }.distinct().toMutableList()
                    val allRetailers = value.map { it.retailer }.toSet()
                    barcodes.remove(key)

                    val productsInOtherBarcodes =
                        infoGroupedByBarcode.filter {
                            it.key in barcodes
                        }.values.flatten()
                            .distinct()
                            .filter { it !in value && it.retailer !in allRetailers }
                    value.addAll(productsInOtherBarcodes)

                    skipBarcodes.addAll(productsInOtherBarcodes.flatMap { it.barcodes!! }
                        .distinct())
                }
            }

            skipBarcodes.forEach { infoGroupedByBarcode.remove(it) }

            products.addAll(infoGroupedByBarcode.values.map { Product(it) })
            products.addAll(retailerProductInfo.map { Product(mutableListOf(it)) })

            products.forEach { (information) ->
                information!!.removeAll { (retailer, id, _, _, _, _, _, _, _, _, _, _, _) ->
                    products.flatMap { it.information!! }
                        .count { it.id == id && it.retailer == retailer } > 1
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
        val products = productMap.values.toMutableList()

        val time = measureTime {
            val productWithMatcherGroup = products.associateWith { (information) ->
                information!!.map { MatcherGrouping(it) }.toMutableSet()
            }

            productWithMatcherGroup.forEach { map ->
                val retailersForProduct = map.key.information!!.map { it.retailer }
                val matches = productWithMatcherGroup.filter { match ->
                    match.key.information!!.isNotEmpty() &&
                            match != map && match.key.information!!.none {
                        retailersForProduct.contains(
                            it.retailer
                        )
                    } && match.value.intersect(map.value).isNotEmpty()
                }

                if (matches.isNotEmpty()) {
                    if (matches.size > 1) {
                        val firstMatch = matches.keys.first()
                        val firstMatchValues = matches[firstMatch]
                        matches.keys.forEachIndexed { index, product ->
                            if (index != 0 && product.information!!.none { (retailer, _, _, _, _, _, _, _, _, _, _, _, _) -> firstMatch.information!!.any { retailer == it.retailer } }) {
                                firstMatch.information!!.addAll(product.information!!)
                                products.remove(product)
                                val value = matches.filterKeys { it == product }.values.first()
                                firstMatchValues!!.addAll(value)
                                value.clear()
                                product.information!!.clear()
                            }
                        }
                    }

                    val match = matches.keys.first()
                    match.information!!.addAll(map.key.information!!)
                    matches.filterKeys { it == match }.values.first().addAll(map.value)
                    products.remove(map.key)
                    map.key.information!!.clear()
                    map.value.clear()
                }
            }
        }

        printStatus(products, retailers)
        print("Barcode matching took ${time.toString(DurationUnit.SECONDS, 1)}")

        val mappedProducts = mapProducts(products)

        return Pair(retailers, mappedProducts)
    }

    private fun mapProducts(products: List<Product>): Map<String, Product> {
        return products.associateBy { (information) ->
            val barcodes =
                information!!.filter { it.barcodes != null }.flatMap { it.barcodes!! }

            if (barcodes.isNotEmpty()) {
                val barcodeUsage = barcodes.groupingBy { it }.eachCount()

                barcodeUsage.maxBy { it.value }.key
            } else {
                val info = information.first()
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
            products.filter { (information) -> information!!.all { it.saleType == SaleType.EACH } }
        println("${
            groceryProducts.count { it.information!!.size > 1 }
                .toDouble() / groceryProducts.size.toDouble() * 100
        }% of products sold by unit matched.")

        retailers.forEach { (key, value) ->
            val total =
                products.count { (information) -> information?.any { it.retailer == key } == true }
            val totalMatched =
                products.count { (information) -> information?.any { it.retailer == key } == true && information.size > 1 }

            println("For ${value.name} ${totalMatched.toDouble() / total.toDouble() * 100}% matched.")
        }

        println(
            "${
                products.count { it.information!!.size > 1 }
                    .toDouble() / products.size.toDouble() * 100
            }% of products matched."
        )
    }
}