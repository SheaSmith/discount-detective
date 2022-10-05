package com.example.cosc345.scraper.scrapertests.shopify

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.shopify.PrimeRangeFreshScraper
import com.example.cosc345.scraper.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Prime Range Fresh tests")
class PrimeRangeFreshUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = PrimeRangeFreshScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Prime Range Fresh has stores")
    fun `Prime Range Fresh has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Prime Range Fresh stores have required fields")
    fun `Prime Range Fresh stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Prime Range Fresh has products")
    fun `Prime Range Fresh has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Prime Range Fresh products have prices")
    fun `Prime Range Fresh products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Prime Range Fresh products have required fields")
    fun `Prime Range Fresh products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}