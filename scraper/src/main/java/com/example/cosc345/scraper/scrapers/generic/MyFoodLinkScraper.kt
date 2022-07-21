package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.shared.models.Retailer

abstract class MyFoodLinkScraper(
    private val id: String,
    private val retailer: Retailer,
    private val shopListUrl: String
)