package com.example.cosc345project.scrapertests

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.FourSquareScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Four Square tests")
class FourSquareUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = FourSquareScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Four Square has stores")
    fun `Four Square has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Four Square stores have required fields")
    fun `Four Square stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Four Square has products")
    fun `Four Square has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Four Square products have prices")
    fun `Four Square products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Four Square products have required fields")
    fun `Four Square products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}