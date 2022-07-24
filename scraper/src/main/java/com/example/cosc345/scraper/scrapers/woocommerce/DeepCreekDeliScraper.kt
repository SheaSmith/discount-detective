package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

class DeepCreekDeliScraper : WooCommerceScraper(
    "deep-creek-deli",
    Retailer(
        "deep-creek-deli", true, listOf(
            Store(
                "deep-creek-deli",
                "Deep Creek Deli",
                "35 North Road, North East Valley, Dunedin 9010",
                -45.8541306,
                170.5154902,
                true
            )
        )
    ),"https://www.deepcreekdeli.co.nz"
)
