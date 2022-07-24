package com.example.cosc345project

import com.example.cosc345.scraper.scrapers.wooCommerce.*
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

    @Test
    fun `Couplands has products`() {
        runBlocking {
            val result = CouplandsScraper(mapOf()).runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }
    @Test
    fun `Origin Food has products`() {
        runBlocking {
            val result = OriginFoodScraper(mapOf()).runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }
    @Test
    fun `Taste Nature has products`() {
        runBlocking {
            val result = TasteNatureScraper(mapOf()).runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }
    @Test
    fun `Deep Creek Deli has products`() {
        runBlocking {
            val result = DeepCreekDeliScraper(mapOf()).runScraper()
            assert(result.productInformation.isNotEmpty())
        }
    }

}