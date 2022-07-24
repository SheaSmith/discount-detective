package com.example.cosc345project

import com.example.cosc345.scraper.scrapers.wooCommerce.HarbourFishScraper
import com.example.cosc345.scraper.scrapers.wooCommerce.MadButcherScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class WooComUnitTest {
    @Test
    fun `Harbour fish has products`() {
        runBlocking {
            val result = HarbourFishScraper(mapOf()).runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }

    @Test
    fun `Mad Butcher has products`() {
        runBlocking {
            val result = MadButcherScraper(mapOf()).runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }

}