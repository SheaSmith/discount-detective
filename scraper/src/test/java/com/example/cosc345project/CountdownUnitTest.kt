package com.example.cosc345project

import com.example.cosc345.scraper.scrapers.CountdownScraper
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CountdownUnitTest {
    @Test
    fun `countdown has products`() {
        runBlocking {
            val response = CountdownScraper().runScraper()
            assert(response.productInformation.isNotEmpty())
            assert(!response.retailer.stores.isNullOrEmpty())
        }
    }
}