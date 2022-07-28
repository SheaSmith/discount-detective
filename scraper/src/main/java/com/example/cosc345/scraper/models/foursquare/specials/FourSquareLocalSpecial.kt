package com.example.cosc345.scraper.models.foursquare.specials

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * A specific Four Square local special.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class FourSquareLocalSpecial(
    /**
     * The name of the product.
     */
    @Selector(".sxa-specials-card__heading")
    var name: String? = null,

    /**
     * The relative path of the image, not including the base URL.
     */
    @Selector(".image-removed-sizes", attr = "src")
    var imagePath: String? = null,

    /**
     * The dollars component of the price, for example for $5.45, this will be 5.
     */
    @Selector(".sxa-specials-card__pricing-dollars", regex = "\\\$?(\\d+)")
    var dollars: Int? = null,

    /**
     * The cents component of the price, for example for $5.45, this will be 45.
     */
    @Selector(".sxa-specials-card__pricing-cents", regex = "(\\d+)")
    var cents: Int? = null,

    /**
     * The sale type, for example if it sold by weight or not.
     */
    @Selector(".sxa-specials-card__pricing-unit")
    var saleType: String? = null
)
