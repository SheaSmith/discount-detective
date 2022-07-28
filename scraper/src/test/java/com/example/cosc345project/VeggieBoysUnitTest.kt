package com.example.cosc345project

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.VeggieBoysScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Veggie Boys tests")
class VeggieBoysUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = VeggieBoysScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Veggie Boys has stores")
    fun `Veggie Boys has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Veggie Boys stores have required fields")
    fun `Veggie Boys stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Veggie Boys has products")
    fun `Veggie Boys has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Veggie Boys products have prices")
    fun `Veggie Boys products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Veggie Boys products have required fields")
    fun `Veggie Boys products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}