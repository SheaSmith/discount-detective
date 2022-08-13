package com.example.cosc345.scraper.scrapertests.foodstuffs

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.foodstuffs.PakNSaveScraper
import com.example.cosc345.scraper.scrapertests.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Pak'nSave tests")
class PakNSaveUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = PakNSaveScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Pak'nSave has stores")
    fun `Pak'nSave has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Pak'nSave stores have required fields")
    fun `Pak'nSave stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Pak'nSave has products")
    fun `Pak'nSave has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Pak'nSave products have prices")
    fun `Pak'nSave products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Pak'nSave products have required fields")
    fun `Pak'nSave products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation)
}