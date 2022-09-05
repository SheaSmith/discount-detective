package com.example.cosc345.scraper.scrapers.foodstuffs

import com.example.cosc345.scraper.scrapers.generic.FoodStuffsScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer

/**
 * The New World specific implementation of the generic [FoodStuffsScraper], which basically just passes some basic information to the generic scraper.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper.
 */
class NewWorldScraper : FoodStuffsScraper(
    "new-world",
    Retailer(
        name = "New World",
        automated = true,
        verified = false,
        colourLight = 0xFFffdad7,
        onColourLight = 0xFF410004,
        colourDark = 0xFF930015,
        onColourDark = 0xFFffdad7,
        initialism = "NW",
        local = false
    ),
    "prod-online-nw-products-index",
    "prod-sitecore-nw-category-index",
    "917ee3ce-acf1-485e-9916-d208c6c6471a",
    mapOf(
        "New World Centre City" to Region.DUNEDIN,
        "New World Gardens" to Region.DUNEDIN,
        "New World Elles Road" to Region.INVERCARGILL,
        "New World Windsor" to Region.INVERCARGILL
    )
)