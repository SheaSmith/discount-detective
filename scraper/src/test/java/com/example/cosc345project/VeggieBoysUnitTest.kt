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
    fun `Veggie Boys has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    fun `Veggie Boys stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    fun `Veggie Boys has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    fun `Veggie Boys products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    fun `Veggie Boys products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}