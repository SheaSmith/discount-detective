package com.example.cosc345.scraper.scrapertests.shopify

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.shopify.CompleatWellnessScraper
import com.example.cosc345.scraper.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("ComplEat Wellness tests")
class CompleatWellnessUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = CompleatWellnessScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("ComplEat Wellness has stores")
    fun `ComplEat Wellness has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("ComplEat Wellness stores have required fields")
    fun `ComplEat Wellness stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("ComplEat Wellness has products")
    fun `ComplEat Wellness has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("ComplEat Wellness products have prices")
    fun `ComplEat Wellness products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("ComplEat Wellness products have required fields")
    fun `ComplEat Wellness products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}