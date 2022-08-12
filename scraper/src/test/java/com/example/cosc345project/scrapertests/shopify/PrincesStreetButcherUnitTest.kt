package com.example.cosc345project.scrapertests.shopify

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.shopify.PrincesStreetButcherScraper
import com.example.cosc345project.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Princes Street Butcher tests")
class PrincesStreetButcherUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = PrincesStreetButcherScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Princes Street Butcher has stores")
    fun `Princes Street Butcher has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Princes Street Butcher stores have required fields")
    fun `Princes Street Butcher stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Princes Street Butcher has products")
    fun `Princes Street Butcher has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Princes Street Butcher products have prices")
    fun `Princes Street Butcher products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Princes Street Butcher products have required fields")
    fun `Princes Street Butcher products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}