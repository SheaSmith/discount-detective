package com.example.cosc345.scraper.scrapers.myfoodlink

import com.example.cosc345.scraper.scrapers.generic.MyFoodLinkScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer

/**
 * The SuperValue specific implementation of the [MyFoodLinkScraper] that just passes through information to it.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper.
 */
class SuperValueScraper : MyFoodLinkScraper(
    "supervalue",
    Retailer(
        name = "SuperValue",
        automated = true,
        verified = false,
        colourLight = 0xFFffdad4,
        onColourLight = 0xFF400100,
        colourDark = 0xFF920600,
        onColourDark = 0xFFffdad4,
        initialism = "SV",
        local = true
    ),
    "https://www.supervalue.co.nz",
    mapOf(
        "SuperValue Plaza" to Region.INVERCARGILL
    )
)