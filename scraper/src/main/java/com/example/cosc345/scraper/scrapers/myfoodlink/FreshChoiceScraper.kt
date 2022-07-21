package com.example.cosc345.scraper.scrapers.myfoodlink

import com.example.cosc345.scraper.scrapers.generic.MyFoodLinkScraper
import com.example.cosc345.shared.models.Retailer

class FreshChoiceScraper : MyFoodLinkScraper(
    "freshchoice",
    Retailer(
        "FreshChoice",
        true
    ),
    "https://www.freshchoice.co.nz",
    arrayOf("FreshChoice Roslyn")
)