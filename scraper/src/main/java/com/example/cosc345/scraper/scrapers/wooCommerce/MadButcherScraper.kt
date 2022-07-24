package com.example.cosc345.scraper.scrapers.wooCommerce

import com.example.cosc345.scraper.scrapers.generic.WooComScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class MadButcherScraper(allProducts: Map<String, RetailerProductInformation>) : WooComScraper(
    "mad-butcher",
    Retailer(
        "Mad Butcher", true, listOf(
            Store(
                "mad-butcher",
                "Mad Butcher",
                "280 Andersons Bay Road, South Dunedin",
                -45.8910911,
                170.5030409,
                true
            )
        )
    ),"https://madbutcher.co.nz/dunedin/"
)
