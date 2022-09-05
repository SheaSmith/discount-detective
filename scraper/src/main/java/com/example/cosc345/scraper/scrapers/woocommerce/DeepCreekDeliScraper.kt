package com.example.cosc345.scraper.scrapers.woocommerce

import com.example.cosc345.scraper.scrapers.generic.WooCommerceScraper
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store
/**
 * The Deep Creek Deli specific implementation of the [WooCommerceScraper], which essentially just passes information through to it.
 *
 * @author William Hadden
 * @constructor Create a new instance of this scraper.
 */
class DeepCreekDeliScraper : WooCommerceScraper(
    "deep-creek-deli",
    Retailer(
        name = "Deep Creek Deli",
        automated = true,
        verified = false,
        stores = listOf(
            Store(
                "deep-creek-deli",
                "Deep Creek Deli",
                "35 North Road, North East Valley, Dunedin 9010",
                -45.8541306,
                170.5154902,
                true,
                Region.DUNEDIN
            )
        ),
        colourLight = 0xFFffe164,
        onColourLight = 0xFF221b00,
        colourDark = 0xFF544600,
        onColourDark = 0xFFffe164,
        initialism = "DC",
        local = true
    ),"https://www.deepcreekdeli.co.nz"
)
