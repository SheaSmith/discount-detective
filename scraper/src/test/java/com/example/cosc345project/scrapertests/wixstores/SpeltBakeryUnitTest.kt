package com.example.cosc345project.scrapertests.wixstores

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.wixstores.SpeltBakeryScraper
import com.example.cosc345project.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Spelt Bakery tests")
class SpeltBakeryUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = SpeltBakeryScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Spelt Bakery has stores")
    fun `Spelt Bakery has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Spelt Bakery stores have required fields")
    fun `Spelt Bakery stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Spelt Bakery has products")
    fun `Spelt Bakery has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Spelt Bakery products have prices")
    fun `Spelt Bakery products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Spelt Bakery products have required fields")
    fun `Spelt Bakery products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}