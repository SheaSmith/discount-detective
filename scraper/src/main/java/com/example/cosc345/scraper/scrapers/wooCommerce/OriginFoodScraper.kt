package com.example.cosc345.scraper.scrapers.wooCommerce

import com.example.cosc345.scraper.scrapers.generic.WooComScraper
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

class OriginFoodScraper(allProducts: Map<String, RetailerProductInformation>) : WooComScraper(
    "origin-food",
    Retailer(
        "Origin Food", true, listOf(
            Store(
                "origin-food",
                "Origin Food",
                "4 Wharf Street",
                -45.8837599,
                170.5047172,
                true
            )
        )
    ),"https://originfood.co.nz"
)