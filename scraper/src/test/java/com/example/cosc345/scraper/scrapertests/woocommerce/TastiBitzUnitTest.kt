package com.example.cosc345.scraper.scrapertests.woocommerce

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.woocommerce.TastiBitzScraper
import com.example.cosc345.scraper.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Tasti Bitz tests")
class TastiBitzUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = TastiBitzScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Tasti Bitz has stores")
    fun `Tasti Bitz has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Tasti Bitz stores have required fields")
    fun `Tasti Bitz stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Tasti Bitz has products")
    fun `Tasti Bitz has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Tasti Bitz products have prices")
    fun `Tasti Bitz products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Tasti Bitz products have required fields")
    fun `Tasti Bitz products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}