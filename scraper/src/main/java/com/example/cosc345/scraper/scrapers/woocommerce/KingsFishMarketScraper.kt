package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Kings Fish Market specific implementation of the [WooCommerceScraper], which essentially just passes information through to it.
 */
class KingsFishMarketScraper : WooCommerceScraper(
    "kings-fish-market",
    Retailer(
        "Kings Fish Market",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "kings-fish-market",
                "Kings Fish Market",
                "59 Ythan Street, Appleby, Invercargill 9812",
                -46.4164672,
                168.3566358,
                true,
                Region.INVERCARGILL
            )
        ),
        colourLight = 0xFFd0e4ff,
        onColourLight = 0xFF001d35,
        colourDark = 0xFF00497b,
        onColourDark = 0xFFd0e4ff,
        initialism = "KF",
        local = true
    ),
    "https://kingsfish.co.nz"
)