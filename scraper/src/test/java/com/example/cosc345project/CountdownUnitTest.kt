package com.example.cosc345project

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.CountdownScraper
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
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
    fun `Countdown has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    fun `Countdown stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    fun `Countdown has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    fun `Countdown products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    fun `Countdown products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}