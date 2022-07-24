package com.example.cosc345.scraper.scrapers.wooCommerce

import com.example.cosc345.scraper.scrapers.generic.WooComScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class CouplandsScraper(allProducts: Map<String, RetailerProductInformation>) : WooComScraper(
    "couplands",
    Retailer(
        "Couplands", true, listOf(
            Store(
                "couplands-kv",
                "Couplands",
                "560 Kaikorai Rd",
                -45.8864384,
                170.4666199,
                true
            ),
            Store(
                "couplands-ab",
                "Couplands",
                "446 Andersons Bay",
                -45.8837276,
                170.4878583,
                true
            )
        )
    ),"https://www.couplands.co.nz"
)
