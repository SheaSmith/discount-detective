package com.example.cosc345.scraper.scrapers.myfoodlink

import com.example.cosc345.scraper.scrapers.generic.MyFoodLinkScraper
import com.example.cosc345.shared.models.Retailer

class SuperValueScraper : MyFoodLinkScraper(
    "supervalue",
    Retailer(
        "SuperValue",
        true
    ),
    "https://www.supervalue.co.nz",
    arrayOf("SuperValue Plaza")
)