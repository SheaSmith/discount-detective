package com.example.cosc345.scraper.models.veggieboys

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * A product from Veggie Boys.
 *
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class VeggieBoysProduct(
    /**
     * The name of the product.
     */
    @Selector("h5")
    var name: String? = null,

    /**
     * The price of the product.
     */
    @Selector(".product-price", regex = "\\\$([\\d.]+)")
    var price: Double? = null,

    /**
     * The relative path of the image.
     */
    @Selector(".mediaholder > a > img", attr = "src")
    var imagePath: String? = null,

    /**
     * The unique ID for this product.
     */
    @Selector("a", attr = "href", regex = "(\\d+)")
    var id: String? = null,

    /**
     * The URL path for this product.
     */
    @Selector("a", attr = "href", regex = "\\/product\\/(.+)")
    var href: String? = null,

    /**
     * If this value is not null, then the product is currently on special.
     */
    @Selector("corner-ribbon")
    var onSpecial: String? = null
)
