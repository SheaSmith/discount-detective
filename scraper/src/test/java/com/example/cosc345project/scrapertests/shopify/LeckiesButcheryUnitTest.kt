package com.example.cosc345project.scrapertests.shopify

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.shopify.LeckiesButcheryScraper
import com.example.cosc345project.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Leckies Butchery tests")
class LeckiesButcheryUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = LeckiesButcheryScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Leckies Butchery has stores")
    fun `Leckies Butchery has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Leckies Butchery stores have required fields")
    fun `Leckies Butchery stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Leckies Butchery has products")
    fun `Leckies Butchery has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Leckies Butchery products have prices")
    fun `Leckies Butchery products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Leckies Butchery products have required fields")
    fun `Leckies Butchery products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}