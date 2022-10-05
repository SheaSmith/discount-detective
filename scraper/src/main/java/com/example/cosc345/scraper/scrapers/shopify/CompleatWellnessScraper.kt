package com.example.cosc345.scraper.scrapers.shopify

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Compleat Wellness specific implementation of the [ShopifyScraper] that just passes through some information to it.
 *
 * @constructor Create a new instance of this scraper.
 */
class CompleatWellnessScraper : ShopifyScraper(
    "compleat-wellness",
    Retailer(
        name = "ComplEat Wellness",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "compleat-wellness",
                "ComplEat Wellness",
                "24 Windsor Street, Windsor, Invercargill 9810",
                -46.397012,
                168.365364,
                true,
                Region.INVERCARGILL
            )
        ),
        colourLight = 0xFFffdcbd,
        onColourLight = 0xFF2c1600,
        colourDark = 0xFF693c00,
        onColourDark = 0xFFffdcbd,
        initialism = "CW",
        local = true
    ),
    "https://www.compleatwellness.co.nz",
    whiteListedCategories = listOf("GROCERY", "BULK FOODS")
)