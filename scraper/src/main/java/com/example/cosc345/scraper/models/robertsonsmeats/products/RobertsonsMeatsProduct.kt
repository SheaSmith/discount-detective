package com.example.cosc345.scraper.models.robertsonsmeats.products

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * An individual product for Robertsons Meats.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class RobertsonsMeatsProduct(
    /**
     * The name of the product.
     */
    @Selector("p")
    var name: String? = null,

    /**
     * The relative path of the image for this product.
     */
    @Selector("img", attr = "src")
    var imagePath: String? = null,

    /**
     * The price of this product.
     */
    @Selector(".price", regex = "\\$(\\d+)")
    var price: Double? = null,

    /**
     * If there is a value for this property, then the product is sold per-kg.
     */
    @Selector(".weight")
    var weight: String? = null
)
