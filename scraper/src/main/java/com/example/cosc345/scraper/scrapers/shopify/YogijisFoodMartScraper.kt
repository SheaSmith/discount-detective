package com.example.cosc345.scraper.scrapers.shopify

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store
/**
 * The Yogiji's Food Mart specific implementation of the [ShopifyScraper] that just passes through some information to it.
 *
 * @constructor Create a new instance of this scraper.
 */
class YogijisFoodMartScraper : ShopifyScraper(
    "yogijis",
    Retailer(
        name = "Yogiji's Food Mart",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "yogijis",
                "Dunedin",
                "229 Leith Street, Dunedin North, Dunedin 9016",
                -45.8680625,
                170.5139159,
                true,
                Region.DUNEDIN
            )
        ),
        colourLight = 0xFFe0e0ff,
        onColourLight = 0xFF00036b,
        colourDark = 0xFF2a33b6,
        onColourDark = 0xFFe0e0ff,
        initialism = "YO",
        local = false
    ),
    "https://yogijis.co.nz"
)