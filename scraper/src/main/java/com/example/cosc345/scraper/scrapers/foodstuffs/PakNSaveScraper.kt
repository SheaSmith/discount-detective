package com.example.cosc345.scraper.scrapers.foodstuffs

import com.example.cosc345.scraper.scrapers.generic.FoodStuffsScraper
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
        "PAK'nSAVE",
        true
    ),
    "prod-online-pns-products-index",
    "prod-sitecore-pns-category-index",
    "b04bb354-835b-4646-a4fd-96dd278292aa",
    arrayOf(
        "PAK'nSAVE Dunedin"
    )
)