package com.example.cosc345.scraper.model

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
     * A list of products for this retailer, including pricing information.
     */
    val productInformation: List<RetailerProductInformation>
)
