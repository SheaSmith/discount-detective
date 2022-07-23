package com.example.cosc345project.myfoodlink

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.myfoodlink.FreshChoiceScraper
import com.example.cosc345project.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("FreshChoice tests")
class FreshChoiceUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = FreshChoiceScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("FreshChoice has stores")
    fun `FreshChoice has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("FreshChoice stores have required fields")
    fun `FreshChoice stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("FreshChoice has products")
    fun `FreshChoice has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("FreshChoice products have prices")
    fun `FreshChoice products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("FreshChoice products have required fields")
    fun `FreshChoice products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}