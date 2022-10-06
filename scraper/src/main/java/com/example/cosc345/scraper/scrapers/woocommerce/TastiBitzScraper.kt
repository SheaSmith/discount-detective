package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Tasti Bitz specific implementation of the [WooCommerceScraper], which essentially just passes information through to it.
 */
class TastiBitzScraper : WooCommerceScraper(
    "tasti-bitz",
    Retailer(
        "Tasti Bitz",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "tasti-bitz",
                "Tasti Bitz",
                "67 Deveron Street, Invercargill 9810",
                -46.4092528,
                168.353939,
                true,
                Region.INVERCARGILL
            )
        ),
        colourLight = 0xFFbde9ff,
        onColourLight = 0xFF001f2a,
        colourDark = 0xFF004d64,
        onColourDark = 0xFFbde9ff,
        initialism = "TB",
        local = true
    ),
    "https://tastibitz.co.nz"
)