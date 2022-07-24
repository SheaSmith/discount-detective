package com.example.cosc345.scraper.scrapers.wooCommerce

import com.example.cosc345.scraper.scrapers.generic.WooComScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store


class HarbourFishScraper(allProducts: Map<String, RetailerProductInformation>) : WooComScraper (
    "harbour-fish",
    Retailer(
        "Harbour Fish", true, listOf(
            Store(
                "harbour-fish",
                "Harbour Fish",
                "83 Great King Street",
                -45.8719386,
                170.3749549,
                true
            )
        )
    ),"https://harbourfish.co.nz"
)
