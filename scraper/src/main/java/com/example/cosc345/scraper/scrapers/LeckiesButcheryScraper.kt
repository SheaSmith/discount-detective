package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class LeckiesButcheryScraper() : ShopifyScraper(
    "leckies-butchery",
    Retailer(
        "Leckies Butchery", true, listOf(
            Store(
                "leckies-butchery",
                "Leckies Butchery",
                "153 Forbury Road, St Clair, Dunedin 9012",
                -45.9070219,
                170.4875238,
                true
            )
        )
    ),
    "https://www.leckiesbutchery.co.nz"
)