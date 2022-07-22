package com.example.cosc345project

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.myfoodlink.SuperValueScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("SuperValue tests")
class SuperValueUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = SuperValueScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    fun `SuperValue has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    fun `SuperValue stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    fun `SuperValue has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    fun `SuperValue products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    fun `SuperValue products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}