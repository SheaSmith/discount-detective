package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

class CouplandsScraper : WooCommerceScraper(
    "couplands",
    Retailer(
        "Couplands", true, listOf(
            Store(
                "couplands-kv",
                "Couplands",
                "560 Kaikorai Rd, Kenmure, Dunedin 9011",
                -45.8864384,
                170.4666199,
                true
            ),
            Store(
                "couplands-ab",
                "Couplands",
                "446 Andersons Bay Rd, South Dunedin, Dunedin 9012",
                -45.8837276,
                170.4878583,
                true
            )
        )
    ),"https://www.couplands.co.nz"
)
