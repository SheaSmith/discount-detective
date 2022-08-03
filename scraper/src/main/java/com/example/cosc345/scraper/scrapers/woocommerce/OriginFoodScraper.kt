package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Origin Foods specific implementation of the [WooCommerceScraper], which essentially just passes information through to it.
 *
 * @author William Hadden
 * @constructor Create a new instance of this scraper.
 */
class OriginFoodScraper : WooCommerceScraper(
    "origin-food",
    Retailer(
        "Origin Food", true, listOf(
            Store(
                "origin-food",
                "Origin Food",
                "4 Wharf Street, Central Dunedin, Dunedin 9016",
                -45.8837599,
                170.5047172,
                true
            )
        )
    ), "https://originfood.co.nz",
    listOf(163)
)