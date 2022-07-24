package com.example.cosc345.scraper.scrapers.shopify

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

class YogijisFoodMartScraper : ShopifyScraper(
    "yogijis",
    Retailer(
        "Yogiji's Food Mart",
        true,
        listOf(
            Store(
                "yogijis",
                "Yogiji's Food Mart Dunedin",
                "229 Leith Street, Dunedin North, Dunedin 9016",
                -45.8680625,
                170.5139159,
                true
            )
        )
    ),
    "https://yogijis.co.nz"
)