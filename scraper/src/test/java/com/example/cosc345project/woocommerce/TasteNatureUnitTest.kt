package com.example.cosc345project.woocommerce

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.woocommerce.TasteNatureScraper
import com.example.cosc345project.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Taste Nature tests")
class TasteNatureUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = TasteNatureScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Taste Nature has stores")
    fun `Taste Nature has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Taste Nature stores have required fields")
    fun `Taste Nature stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Taste Nature has products")
    fun `Taste Nature has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Taste Nature products have prices")
    fun `Taste Nature products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Taste Nature products have required fields")
    fun `Taste Nature products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}