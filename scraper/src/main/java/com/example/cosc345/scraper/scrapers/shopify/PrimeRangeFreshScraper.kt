package com.example.cosc345.scraper.scrapers.shopify

import com.example.cosc345.scraper.scrapers.generic.ShopifyScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * The Prime Range Fresh specific implementation of the [ShopifyScraper] that just passes through some information to it.
 *
 * @constructor Create a new instance of this scraper.
 */
class PrimeRangeFreshScraper : ShopifyScraper(
    "prime-range-fresh",
    Retailer(
        name = "Prime Range Fresh",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "prime-range-fresh",
                "Prime Range Fresh",
                "808 North Road, Lorneville, Invercargill 9876",
                -46.3513898,
                168.3464725,
                true,
                Region.INVERCARGILL
            )
        ),
        colourLight = 0xFFffdeab,
        onColourLight = 0xFF271900,
        colourDark = 0xFF5f4100,
        onColourDark = 0xFFffdeab,
        initialism = "PR",
        local = true
    ),
    "https://primerangefresh.co.nz"
)