package com.example.cosc345.scraper.scrapers.myfoodlink

import com.example.cosc345.scraper.scrapers.generic.MyFoodLinkScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer

/**
 * The FreshChoice specific implementation of the [MyFoodLinkScraper] that just passes through information to it.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper.
 */
class FreshChoiceScraper : MyFoodLinkScraper(
    "freshchoice",
    Retailer(
        name = "FreshChoice",
        automated = true,
        verified = false,
        colourLight = 0xFFc5e7ff,
        onColourLight = 0xFF001e2d,
        colourDark = 0xFF004c6a,
        onColourDark = 0xFFc5e7ff,
        initialism = "FC",
        local = false
    ),
    "https://www.freshchoice.co.nz",
    mapOf(
        "FreshChoice Roslyn" to Region.DUNEDIN,
        "FreshChoice Green Island" to Region.DUNEDIN
    )
)