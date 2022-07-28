package com.example.cosc345.scraper.scrapers.myfoodlink

import com.example.cosc345.scraper.scrapers.generic.MyFoodLinkScraper
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
        "SuperValue",
        true
    ),
    "https://www.supervalue.co.nz",
    arrayOf("SuperValue Plaza")
)