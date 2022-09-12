package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Multi-buy information about a product, for example 2 for $5.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownProductMultiBuy(
    /**
     * The quantity you can buy for this discount, for example 2 in a 2 for $5 discount.
     */
    @Json(name = "quantity")
    val quantity: Int,

    /**
     * The price of the multi-buy discount, for example $5 in a 2 for $5 discount.
     */
    @Json(name = "value")
    val price: Double
)
