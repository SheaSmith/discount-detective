package com.example.cosc345.scraper.scrapers.wooCommerce

import com.example.cosc345.scraper.scrapers.generic.WooComScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class TasteNatureScraper(allProducts: Map<String, RetailerProductInformation>) : WooComScraper(
    "taste nature",
    Retailer(
        "taste-nature", true, listOf(
            Store(
                "taste-nature",
                "Taste Nature",
                "131 High Street",
                -45.8782902,
                170.4984784,
                true
            )
        )
    ),"https://www.tastenature.co.nz"
)
