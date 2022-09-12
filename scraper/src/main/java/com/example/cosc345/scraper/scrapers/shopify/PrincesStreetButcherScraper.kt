package com.example.cosc345.scraper.scrapers.shopify

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Princes Street Butcher specific implementation of the [ShopifyScraper] that just passes through some information to it.
 *
 * @constructor Create a new instance of this scraper.
 */
class PrincesStreetButcherScraper :
    ShopifyScraper(
        "princes-street-butcher",
        Retailer(
            name = "Princes Street Butcher",
            automated = true,
            verified = false,
            stores = listOf(
                Store(
                    "princes-street-butcher",
                    "Princes Street Butcher",
                    "416 Princes Street, Central Dunedin, Dunedin 9016",
                    -45.880502,
                    170.4998258,
                    true,
                    Region.DUNEDIN
                )
            ),
            colourLight = 0xFF97f0ff,
            onColourLight = 0xFF001f24,
            colourDark = 0xFF004f58,
            onColourDark = 0xFF97f0ff,
            initialism = "PB",
            local = true
        ),
        "https://www.princesstreetbutcher.co.nz"
    )