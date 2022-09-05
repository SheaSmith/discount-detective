package com.example.cosc345.scraper.scrapers.foodstuffs

import com.example.cosc345.scraper.scrapers.generic.FoodStuffsScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer

/**
 * The PAK'nSAVE specific implementation of the generic [FoodStuffsScraper], which basically just passes some basic information to the generic scraper.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper.
 */
class PakNSaveScraper : FoodStuffsScraper(
    "paknsave",
    Retailer(
        name = "PAK'nSAVE",
        automated = true,
        verified = false,
        colourLight = 0xFFffe170,
        onColourLight = 0xFF221b00,
        colourDark = 0xFF544600,
        onColourDark = 0xFFffe170,
        initialism = "PS",
        local = false
    ),
    "prod-online-pns-products-index",
    "prod-sitecore-pns-category-index",
    "b04bb354-835b-4646-a4fd-96dd278292aa",
    mapOf(
        "PAK'nSAVE Dunedin" to Region.DUNEDIN,
        "PAK'nSAVE Invercargill" to Region.INVERCARGILL
    )
)