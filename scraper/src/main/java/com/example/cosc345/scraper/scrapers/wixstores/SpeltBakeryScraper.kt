package com.example.cosc345.scraper.scrapers.wixstores

import com.example.cosc345.scraper.scrapers.generic.WixStoresScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

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