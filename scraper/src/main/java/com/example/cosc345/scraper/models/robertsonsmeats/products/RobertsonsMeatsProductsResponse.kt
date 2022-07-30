package com.example.cosc345.scraper.models.robertsonsmeats.products

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * The response that contains the individual products.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class RobertsonsMeatsProductsResponse(
    /**
     * The list of products.
     */
    @Selector(".items > li")
    var products: List<RobertsonsMeatsProduct>? = null
)
