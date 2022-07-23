package com.example.cosc345project.myfoodlink

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.myfoodlink.SuperValueScraper
import com.example.cosc345project.BaseTests
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
    @DisplayName("SuperValue has stores")
    fun `SuperValue has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("SuperValue stores have required fields")
    fun `SuperValue stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("SuperValue has products")
    fun `SuperValue has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("SuperValue products have prices")
    fun `SuperValue products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("SuperValue products have required fields")
    fun `SuperValue products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}