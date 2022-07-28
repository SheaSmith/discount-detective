package com.example.cosc345.scraper.models.madbutcher

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * A particular product at the Mad Butcher.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class MadButcherProduct(
    /**
     * The URL for this product, which we use as a unique ID.
     */
    @Selector(".woocommerce-LoopProduct-link", attr = "href")
    var href: String? = null,

    /**
     * The price of the product, including the sale type (for example, per kg).
     */
    @Selector(".price")
    var price: String? = null
)
