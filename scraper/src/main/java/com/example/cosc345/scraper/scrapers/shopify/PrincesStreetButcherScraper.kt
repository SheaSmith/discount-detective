package com.example.cosc345.scraper.scrapers.shopify

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Princes Street Butcher specific implementation of the [ShopifyScraper] that just passes through some information to it.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper.
 */
class PrincesStreetButcherScraper :
    ShopifyScraper(
        "princes-street-butcher",
        Retailer(
            "Princes Street Butcher",
            true,
            listOf(
                Store(
                    "princes-street-butcher",
                    "Princes Street Butcher",
                    "416 Princes Street, Central Dunedin, Dunedin 9016",
                    -45.880502,
                    170.4998258,
                    true
                )
            )
        ),
        "https://www.princesstreetbutcher.co.nz"
    )