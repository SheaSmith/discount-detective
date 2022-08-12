package com.example.cosc345project.scrapertests.woocommerce

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.woocommerce.HarbourFishScraper
import com.example.cosc345project.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Harbour Fish tests")
class HarbourFishUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = HarbourFishScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Harbour Fish has stores")
    fun `Harbour Fish has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Harbour Fish stores have required fields")
    fun `Harbour Fish stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Harbour Fish has products")
    fun `Harbour Fish has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Harbour Fish products have prices")
    fun `Harbour Fish products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Harbour Fish products have required fields")
    fun `Harbour Fish products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}