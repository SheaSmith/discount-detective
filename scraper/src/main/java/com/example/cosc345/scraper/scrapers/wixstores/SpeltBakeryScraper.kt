package com.example.cosc345.scraper.scrapers.wixstores

import com.example.cosc345.scraper.scrapers.generic.WixStoresScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Spelt Bakery specific implementation of the generic [WixStoresScraper], which essentially just passes through information to it.
 *
 * @constructor Create a new instance of this scraper.
 */
class SpeltBakeryScraper : WixStoresScraper(
    "spelt-bakery",
    Retailer(
        name = "Spelt Bakery",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "spelt-bakery",
                "Spelt Bakery",
                "481 Highgate, Maori Hill, Dunedin 9010",
                -45.8611142,
                170.4958543,
                true,
                Region.DUNEDIN
            )
        ),
        colourLight = 0xFFffe07e,
        onColourLight = 0xFF231b00,
        colourDark = 0xFF564500,
        onColourDark = 0xFFffe07e,
        initialism = "SB",
        local = true
    ),
    "https://www.speltbakery.com"
)