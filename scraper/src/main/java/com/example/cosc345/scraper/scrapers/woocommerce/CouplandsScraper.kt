package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.*

/**
 * The Couplands specific implementation of the [WooCommerceScraper], which essentially just passes information through to it.
 *
 * @author William Hadden
 * @constructor Create a new instance of this scraper.
 */
class CouplandsScraper : WooCommerceScraper(
    "couplands",
    Retailer(
        name = "Couplands",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "couplands-kv",
                "Kaikorai Valley",
                "560 Kaikorai Rd, Kenmure, Dunedin 9011",
                -45.8864384,
                170.4666199,
                true,
                Region.DUNEDIN
            ),
            Store(
                "couplands-ab",
                "Andersons Bay",
                "446 Andersons Bay Rd, South Dunedin, Dunedin 9012",
                -45.8837276,
                170.4878583,
                true,
                Region.DUNEDIN
            ),
            Store(
                "couplands-ivc",
                "Invercargill",
                "85 North Road, Prestonville, Invercargill 9810",
                46.3860511,
                168.3455993,
                true,
                Region.INVERCARGILL
            )
        ),
        colourLight = 0xFFffdad7,
        onColourLight = 0xFF410006,
        colourDark = 0xFF930018,
        onColourDark = 0xFFffdad7,
        initialism = "CL",
        local = false
    ), "https://www.couplands.co.nz"
) {
    override suspend fun runScraper(): ScraperResult {
        val result = super.runScraper()

        val productsToRemove = mutableListOf<RetailerProductInformation>()
        result.productInformation.forEach {
            if (it.name!!.contains("Our Daily Fresh", ignoreCase = true) ||
                it.name!!.contains("Our Country Harvest", ignoreCase = true) ||
                it.name!!.contains("Our Southern Plains", ignoreCase = true)
            ) {
                if (!it.name!!.contains("Bread", ignoreCase = true)) {
                    it.name = "${it.name} Bread"
                }

                if (it.quantity != null && Units.PACK.regex.matches(it.quantity!!)) {
                    productsToRemove.add(it)
                }
            }
        }

        result.productInformation.removeAll(productsToRemove)

        return result
    }
}
