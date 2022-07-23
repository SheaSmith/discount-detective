package com.example.cosc345project

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.WarehouseScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("The Warehouse tests")
class WarehouseUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = WarehouseScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("The Warehouse has stores")
    fun `The Warehouse has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("The Warehouse stores have required fields")
    fun `The Warehouse stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("The Warehouse has products")
    fun `The Warehouse has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("The Warehouse products have prices")
    fun `The Warehouse products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("The Warehouse products have required fields")
    fun `The Warehouse products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}