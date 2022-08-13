package com.example.cosc345.scraper.scrapertests

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.CountdownScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Countdown tests")
class CountdownUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = CountdownScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Countdown has stores")
    fun `Countdown has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Countdown stores have required fields")
    fun `Countdown stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Countdown has products")
    fun `Countdown has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Countdown products have prices")
    fun `Countdown products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Countdown products have required fields")
    fun `Countdown products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}