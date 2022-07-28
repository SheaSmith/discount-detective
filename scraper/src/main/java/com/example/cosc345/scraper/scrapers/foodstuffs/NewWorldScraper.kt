package com.example.cosc345.scraper.scrapers.foodstuffs

import com.example.cosc345.scraper.scrapers.generic.FoodStuffsScraper
import com.example.cosc345.shared.models.Retailer

class NewWorldScraper : FoodStuffsScraper(
    "new-world",
    Retailer(
        name = "New World",
        automated = true
    ),
    "prod-online-nw-products-index",
    "prod-sitecore-nw-category-index",
    "917ee3ce-acf1-485e-9916-d208c6c6471a",
    arrayOf(
        "New World Centre City",
        "New World Gardens"
    )
)