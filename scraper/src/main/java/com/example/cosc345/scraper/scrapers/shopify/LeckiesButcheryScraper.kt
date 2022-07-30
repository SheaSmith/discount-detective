package com.example.cosc345.scraper.scrapers.shopify

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Leckies Butchery specific implementation of the [ShopifyScraper] that just passes through some information to it.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper.
 */
class LeckiesButcheryScraper : ShopifyScraper(
    "leckies-butchery",
    Retailer(
        "Leckies Butchery", true, listOf(
            Store(
                "leckies-butchery",
                "Leckies Butchery",
                "153 Forbury Road, St Clair, Dunedin 9012",
                -45.9070219,
                170.4875238,
                true
            )
        )
    ),
    "https://www.leckiesbutchery.co.nz"
)