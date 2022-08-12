package com.example.cosc345project.scrapertests.woocommerce

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.woocommerce.DeepCreekDeliScraper
import com.example.cosc345project.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Deep Creek Deli tests")
class DeepCreekDeliUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = DeepCreekDeliScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Deep Creek Deli has stores")
    fun `Deep Creek Deli has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Deep Creek Deli stores have required fields")
    fun `Deep Creek Deli stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Deep Creek Deli has products")
    fun `Deep Creek Deli has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Deep Creek Deli products have prices")
    fun `Deep Creek Deli products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Deep Creek Deli products have required fields")
    fun `Deep Creek Deli products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}