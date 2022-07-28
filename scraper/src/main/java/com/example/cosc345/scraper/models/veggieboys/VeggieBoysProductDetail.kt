package com.example.cosc345.scraper.models.veggieboys

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * Details about a particular product.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class VeggieBoysProductDetail(
    /**
     * Whether the product is sold per-kg or not. If it is not null, then it is sold per-kg.
     */
    @Selector(".product-price > small")
    var perKg: String? = null
)
