package com.example.cosc345.scraper.models

import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store

/**
 * The results from the scraper, containing the retailer, store and product information.
 */
data class ScraperResult(
    /**
     * Information about the specific retailer.
     */
    val retailer: Retailer,

    /**
     * A map of the stores, with the unique store ID acting as the key, and the store object as the value.
     */
    val stores: Map<String, Store>,

    /**
     * A map of the products, including pricing information, to special IDs.
     *
     * These IDs are composed of the unique retailer ID combined with the retailer-specific product ID, in the format *a*-*b*, where *a* is the retailer ID and *b* is the product ID. This isn't an ideal solution, but as Firebase requires a unique key for indexing purposes, and because the retailer supplied product IDs are not guaranteed to be unique, we have to use this technique.
     */
    val productInformation: Map<String, RetailerProductInformation>
)
