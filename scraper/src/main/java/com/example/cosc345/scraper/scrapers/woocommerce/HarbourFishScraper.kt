package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Harbour Fish specific implementation of the [WooCommerceScraper], which essentially just passes information through to it.
 *
 * @constructor Create a new instance of this scraper.
 */
class HarbourFishScraper : WooCommerceScraper(
    "harbour-fish",
    Retailer(
        name = "Harbour Fish",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "harbour-fish",
                "Harbour Fish",
                "83 Great King Street, Central Dunedin, Dunedin 9016",
                -45.8719386,
                170.3749549,
                true,
                Region.DUNEDIN
            )
        ),
        colourLight = 0xFFdee0ff,
        onColourLight = 0xFF000f5c,
        colourDark = 0xFF313f90,
        onColourDark = 0xFFdee0ff,
        initialism = "HF",
        local = true
    ),"https://harbourfish.co.nz"
)
