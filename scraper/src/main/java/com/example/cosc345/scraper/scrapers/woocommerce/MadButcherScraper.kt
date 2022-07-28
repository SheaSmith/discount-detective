package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.api.MadButcherApi
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345.shared.models.Store
/**
 * The Mad Butcher specific implementation of the [WooCommerceScraper]. However, as the sale type for individual products at the Mad Butcher doesn't seem to be present in the API, it needs to be scraped from the HTML, so this acts as an extension to the WooCommerce scraper to facilitate this.
 *
 * # Process
 * Essentially the product listing page is loaded, all of the items scraped, and then moved into a map containing the sale type and the ID of the product.
 *
 * The main WooCommerce scraper is then called, which utilises this data.
 *
 * @author William Hadden
 * @author Shea Smith
 * @constructor Create a new instance of this scraper.
 */
class MadButcherScraper : WooCommerceScraper(
    "mad-butcher",
    Retailer(
        "Mad Butcher", true, listOf(
            Store(
                "mad-butcher",
                "Mad Butcher",
                "280 Andersons Bay Road, South Dunedin, Dunedin 9012",
                -45.8910911,
                170.5030409,
                true
            )
        )
    ), "https://madbutcher.co.nz/dunedin/"
) {
    override suspend fun runScraper(): ScraperResult {
        val madButcherService =
            generateHtmlRequest(MadButcherApi::class.java, "https://madbutcher.co.nz/dunedin/")

        var page = 1
        var maxPage = 1

        val saleTypeMap = mutableMapOf<String, SaleType>()
        while (page <= maxPage) {
            val response = madButcherService.getProductsListing(page)

            response.products!!.forEach {
                saleTypeMap[it.href!!] = if (it.price!!.contains(
                        "kg",
                        ignoreCase = true
                    )
                ) SaleType.WEIGHT else SaleType.EACH
            }

            maxPage = response.lastPage!!
            page++
        }

        super.madButcherMap = saleTypeMap
        return super.runScraper()
    }
}