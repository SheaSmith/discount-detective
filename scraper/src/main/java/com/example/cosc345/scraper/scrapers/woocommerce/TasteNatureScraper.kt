package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Taste Nature specific implementation of the [WooCommerceScraper], which essentially just passes information through to it.
 *
 * @author William Hadden
 * @constructor Create a new instance of this scraper.
 */
class TasteNatureScraper : WooCommerceScraper(
    "taste nature",
    Retailer(
        "Taste Nature", true, listOf(
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
