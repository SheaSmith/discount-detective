package com.example.cosc345project.woocommerce

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.woocommerce.MadButcherScraper
import com.example.cosc345project.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Mad Butcher tests")
class MadButcherUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = MadButcherScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Mad Butcher has stores")
    fun `Mad Butcher has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Mad Butcher stores have required fields")
    fun `Mad Butcher stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Mad Butcher has products")
    fun `Mad Butcher has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Mad Butcher products have prices")
    fun `Mad Butcher products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Mad Butcher products have required fields")
    fun `Mad Butcher products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}