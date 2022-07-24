package com.example.cosc345.scraper.scrapers.wooCommerce

import com.example.cosc345.scraper.scrapers.generic.WooComScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class DeepCreekDeliScraper(allProducts: Map<String, RetailerProductInformation>) : WooComScraper(
    "deep-creek-deli",
    Retailer(
        "deep-creek-deli", true, listOf(
            Store(
                "deep-creek-deli",
                "Deep Creek Deli",
                "35 North Road, North East Valley",
                -45.8541306,
                170.5154902,
                true
            )
        )
    ),"https://www.deepcreekdeli.co.nz"
)
