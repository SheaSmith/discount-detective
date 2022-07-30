package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Harbour Fish specific implementation of the [WooCommerceScraper], which essentially just passes information through to it.
 *
 * @author William Hadden
 * @constructor Create a new instance of this scraper.
 */
class HarbourFishScraper : WooCommerceScraper(
    "harbour-fish",
    Retailer(
        "Harbour Fish", true, listOf(
            Store(
                "harbour-fish",
                "Harbour Fish",
                "83 Great King Street, Central Dunedin, Dunedin 9016",
                -45.8719386,
                170.3749549,
                true
            )
        )
    ),"https://harbourfish.co.nz"
)
