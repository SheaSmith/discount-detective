package com.example.cosc345project.scrapertests.woocommerce

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.woocommerce.OriginFoodScraper
import com.example.cosc345project.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Origin Food tests")
class OriginFoodUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = OriginFoodScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Origin Food has stores")
    fun `Origin Food has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Origin Food stores have required fields")
    fun `Origin Food stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Origin Food has products")
    fun `Origin Food has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Origin Food products have prices")
    fun `Origin Food products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Origin Food products have required fields")
    fun `Origin Food products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}