package com.example.cosc345project.scrapertests

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.RobertsonsMeatsScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Robertsons Meats tests")
class RobertsonsMeatsUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = RobertsonsMeatsScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Robertsons Meats has stores")
    fun `Robertsons Meats has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Robertsons Meats stores have required fields")
    fun `Robertsons Meats stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Robertsons Meats has products")
    fun `Robertsons Meats has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Robertsons Meats products have prices")
    fun `Robertsons Meats products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Robertsons Meats products have required fields")
    fun `Robertsons Meats products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}