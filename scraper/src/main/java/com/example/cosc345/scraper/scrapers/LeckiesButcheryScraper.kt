package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class LeckiesButcheryScraper(allProducts: Map<String, RetailerProductInformation>) : ShopifyScraper(
    "ffd54e22-7b18-4492-a3fd-698f79e65b89",
    Retailer("Leckies Butchery", true, mapOf(Pair("ffd54e22-7b18-4492-a3fd-698f79e65b89", true))),
    Store(
        "Leckies Butchery",
        "153 Forbury Road, St Clair, Dunedin 9012",
        -45.9070219,
        170.4875238,
        true
    ),
    "https://www.leckiesbutchery.co.nz",
    allProducts.filter {
        it.key.startsWith(
            String.format(
                "%s-",
                "ffd54e22-7b18-4492-a3fd-698f79e65b89"
            )
        )
    }.values.toList()
)