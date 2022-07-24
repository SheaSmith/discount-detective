package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

class TasteNatureScraper : WooCommerceScraper(
    "taste nature",
    Retailer(
        "taste-nature", true, listOf(
            Store(
                "taste-nature",
                "Taste Nature",
                "131 High Street, Central Dunedin, Dunedin 9016",
                -45.8782902,
                170.4984784,
                true
            )
        )
    ),"https://www.tastenature.co.nz"
)
