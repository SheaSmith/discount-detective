package com.example.cosc345.scraper.models

import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation

/**
 * The results from the scraper, containing the retailer, store and product information.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be needed for Scrapers.
 */
data class ScraperResult(
    /**
     * Information about the specific retailer.
     */
    val retailer: Retailer,

    /**
     * A list of products that are carried by this retailer.
     */
    val productInformation: List<RetailerProductInformation>,

    /**
     * The unique ID of this retailer.
     */
    val retailerId: String
)
