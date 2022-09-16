package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Information about the product price.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownProductPrice(
    /**
     * The original price before any discounts.
     */
    @Json(name = "originalPrice")
    val originalPrice: Double,

    /**
     * The price of the product, taking into account any discounts (if any exist). If there is no discount, this is the same as the original price.
     */
    @Json(name = "salePrice")
    val salePrice: Double,

    /**
     * The difference between the original price and sale price.
     */
    @Json(name = "savePrice")
    val savePrice: Double,

    /**
     * Whether this discount is available to club members only or not.
     */
    @Json(name = "isClubPrice")
    val isClubPrice: Boolean
)
