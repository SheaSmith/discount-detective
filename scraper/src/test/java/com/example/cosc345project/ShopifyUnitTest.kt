package com.example.cosc345project

import com.example.cosc345.scraper.scrapers.shopify.LeckiesButcheryScraper
import com.example.cosc345.scraper.scrapers.shopify.PrincesStreetButcherScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@DisplayName("Shopify tests")
class ShopifyUnitTest {
    @Test
    fun `leckies butchery has products`() {
        runBlocking {
            val result = LeckiesButcheryScraper().runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }

    @Test
    fun `princes street butchery has products`() {
        runBlocking {
            val result = PrincesStreetButcherScraper().runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }
}