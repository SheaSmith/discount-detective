package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.api.MadButcherApi
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345.shared.models.Store

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