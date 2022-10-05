package com.example.cosc345.scraper.scrapertests.woocommerce

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.woocommerce.KingsFishMarketScraper
import com.example.cosc345.scraper.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Kings Fish Market tests")
class KingsFishMarketUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = KingsFishMarketScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Kings Fish Market has stores")
    fun `Kings Fish Market has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Kings Fish Market stores have required fields")
    fun `Kings Fish Market stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Kings Fish Market has products")
    fun `Kings Fish Market has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Kings Fish Market products have prices")
    fun `Kings Fish Market products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Kings Fish Market products have required fields")
    fun `Kings Fish Market products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}