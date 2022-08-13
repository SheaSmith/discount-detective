package com.example.cosc345.scraper.scrapertests.foodstuffs

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.foodstuffs.NewWorldScraper
import com.example.cosc345.scraper.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("New World tests")
class NewWorldUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = NewWorldScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("New World has stores")
    fun `New World has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("New World stores have required fields")
    fun `New World stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("New World has products")
    fun `New World has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("New World products have prices")
    fun `New World products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("New World products have required fields")
    fun `New World products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}