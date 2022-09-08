package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An image for a particular product.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownProductImage(
    /**
     * The URL of the image.
     */
    @Json(name = "big")
    val imageUrl: String
)
