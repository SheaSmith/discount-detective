package com.example.cosc345.scraper

import com.example.cosc345.scraper.models.MatcherGrouping
import com.example.cosc345.scraper.scrapers.*
import com.example.cosc345.scraper.scrapers.foodstuffs.NewWorldScraper
import com.example.cosc345.scraper.scrapers.foodstuffs.PakNSaveScraper
import com.example.cosc345.scraper.scrapers.myfoodlink.FreshChoiceScraper
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

@OptIn(ExperimentalTime::class)
class Matcher {
    suspend fun runScrapers(): Pair<Map<String, Retailer>, List<RetailerProductInformation>> {
        val scrapers = setOf(
            CountdownScraper(),
            NewWorldScraper(),
            PakNSaveScraper(),
            FreshChoiceScraper(),
            // SuperValue disabled for now, as there aren't any in the Dunedin area.
            //SuperValueScraper(),
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

    fun matchBarcodes(
        retailerProductInfo: MutableList<RetailerProductInformation>,
        retailers: Map<String, Retailer>
    ): Pair<Map<String, Retailer>, Map<String, Product>> {
        val products = mutableListOf<Product>()
        val time = measureTime {
            val infoWithBarcodes = retailerProductInfo.filter { it.barcodes != null }
            retailerProductInfo.removeAll(infoWithBarcodes)
            infoWithBarcodes.forEach { info ->
                val barcodes = info.barcodes!!.toSet()
                val productMatches = products.filter { product ->
                    product.information!!.any {
                        it.barcodes?.intersect(barcodes)?.isNotEmpty() == true
                    } && product.information!!.none { it.retailer == info.retailer }
                }

                if (productMatches.isEmpty()) {
                    products.add(Product(arrayListOf(info)))
                } else if (productMatches.size == 1) {
                    productMatches.first().information!!.add(info)
                } else if (productMatches.size >= 2) {
                    val firstMatch = productMatches.first()
                    productMatches.forEachIndexed { index, product ->
                        if (index != 0 && product.information!!.none { retailerInfo -> firstMatch.information!!.any { retailerInfo.retailer == it.retailer } }) {
                            firstMatch.information!!.addAll(product.information!!)
                            products.remove(product)
                        }
                    }

                    productMatches.first().information!!.add(info)
                }
            }

            products.addAll(retailerProductInfo.map { Product(arrayListOf(it)) })

        }

        printStatus(products, retailers)
        print("Barcode matching took ${time.toString(DurationUnit.SECONDS, 1)}")

        val mappedProducts = mapProducts(products)

        return Pair(retailers, mappedProducts)
    }

    fun matchNames(
        productMap: Map<String, Product>,
        retailers: Map<String, Retailer>
    ): Pair<Map<String, Retailer>, Map<String, Product>> {
        val products = productMap.values.toMutableList()

        val time = measureTime {
            val productWithMatcherGroup = products.associateWith { product ->
                product.information!!.map { MatcherGrouping(it) }.toMutableSet()
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
                            if (index != 0 && product.information!!.none { retailerInfo -> firstMatch.information!!.any { retailerInfo.retailer == it.retailer } }) {
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
            println("{${it.key}: ${it.value.size}")
        }

        val groceryProducts =
            products.filter { product -> product.information!!.all { it.saleType == SaleType.EACH } }
        println("${
            groceryProducts.count { it.information!!.size > 1 }
                .toDouble() / groceryProducts.size.toDouble() * 100
        }$ of products sold by unit matched.")

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