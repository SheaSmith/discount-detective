package com.example.cosc345.scraper.scrapers.shopify

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Region
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
        name = "Leckies Butchery",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "leckies-butchery",
                "Leckies Butchery",
                "153 Forbury Road, St Clair, Dunedin 9012",
                -45.9070219,
                170.4875238,
                true,
                Region.DUNEDIN
            )
        ),
        colourLight = 0xFFdce1ff,
        onColourLight = 0xFF00164f,
        colourDark = 0xFF284190,
        onColourDark = 0xFFdce1ff,
        initialism = "LB",
        local = true
    ),
    "https://www.leckiesbutchery.co.nz"
)