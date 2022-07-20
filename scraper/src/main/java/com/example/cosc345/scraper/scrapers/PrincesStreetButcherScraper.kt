package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class PrincesStreetButcherScraper() :
    ShopifyScraper(
        "princes-street-butcher",
        Retailer(
            "Princes Street Butcher",
            true,
            listOf(
                Store(
                    "princes-street-butcher",
                    "Princes Street Butcher",
                    "416 Princes Street, Central Dunedin, Dunedin 9016",
                    -45.880502,
                    170.4998258,
                    true
                )
            )
        ),
        "https://www.princesstreetbutcher.co.nz"
    )