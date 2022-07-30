package com.example.cosc345.scraper.scrapers.myfoodlink

import com.example.cosc345.scraper.scrapers.generic.MyFoodLinkScraper
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
        "FreshChoice",
        true
    ),
    "https://www.freshchoice.co.nz",
    arrayOf("FreshChoice Roslyn", "FreshChoice Green Island")
)