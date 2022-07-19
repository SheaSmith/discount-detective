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
     * A list of products that are carried by this retailer.
     */
    val productInformation: List<RetailerProductInformation>
)
