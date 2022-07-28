package com.example.cosc345.scraper.models.veggieboys

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * The response for the get product request.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class VeggieBoysProductsResponse(
    /**
     * A list of the products.
     */
    @Selector(".product")
    var products: List<VeggieBoysProduct>? = null
)
