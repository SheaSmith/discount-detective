package com.example.cosc345.scraper.scrapers.wixstores

import com.example.cosc345.scraper.scrapers.generic.WixStoresScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Spelt Bakery specific implementation of the generic [WixStoresScraper], which essentially just passes through information to it.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper.
 */
class SpeltBakeryScraper : WixStoresScraper(
    "spelt-bakery",
    Retailer(
        "Spelt Bakery",
        true,
        listOf(
            Store(
                "spelt-bakery",
                "Spelt Bakery",
                "481 Highgate, Maori Hill, Dunedin 9010",
                -45.8611142,
                170.4958543,
                true
            )
        )
    ),
    "https://www.speltbakery.com"
)