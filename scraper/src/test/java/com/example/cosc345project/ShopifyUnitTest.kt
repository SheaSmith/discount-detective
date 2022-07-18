package com.example.cosc345project

import com.example.cosc345.scraper.scrapers.LeckiesButcheryScraper
import com.example.cosc345.scraper.scrapers.PrincesStreetButcherScraper
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ShopifyUnitTest {
    @Test
    fun `leckies butchery has products`() {
        runBlocking {
            val result = LeckiesButcheryScraper(mapOf()).runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }

    @Test
    fun `princes street butchery has products`() {
        runBlocking {
            val result = PrincesStreetButcherScraper(mapOf()).runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }
}