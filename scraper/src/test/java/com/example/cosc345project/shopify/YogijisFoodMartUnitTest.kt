package com.example.cosc345project.shopify

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.shopify.YogijisFoodMartScraper
import com.example.cosc345project.BaseTests
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
@DisplayName("Yogiji's Food Mart tests")
class YogijisFoodMartUnitTest : BaseTests() {
    companion object {
        private var response: ScraperResult? = null
    }

    init {
        if (response == null) {
            runBlocking {
                val time = measureTime {
                    response = YogijisFoodMartScraper().runScraper()
                }

                println("Time taken to get products: ${time.toString(DurationUnit.SECONDS, 1)}")
            }
        }
    }

    @Test
    @DisplayName("Yogiji's Food Mart has stores")
    fun `Yogiji's Food Mart has stores`() =
        assert(!response!!.retailer.stores.isNullOrEmpty())

    @Test
    @DisplayName("Yogiji's Food Mart stores have required fields")
    fun `Yogiji's Food Mart stores have required fields`() =
        allStoresHaveRequiredFields(response!!.retailer.stores!!)

    @Test
    @DisplayName("Yogiji's Food Mart has products")
    fun `Yogiji's Food Mart has products`() =
        assert(response!!.productInformation.isNotEmpty())

    @Test
    @DisplayName("Yogiji's Food Mart products have prices")
    fun `Yogiji's Food Mart products have prices`() =
        assert(allProductsHavePrices(response!!.productInformation))

    @Test
    @DisplayName("Yogiji's Food Mart products have required fields")
    fun `Yogiji's Food Mart products have required fields`() =
        allProductsHaveRequiredFields(response!!.productInformation, false)
}