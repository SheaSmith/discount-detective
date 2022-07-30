package com.example.cosc345.scraper.models.shopify

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An object with metadata about the image, along with the URL.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class ShopifyImage(
    /**
     * The URL of the image, generally hosted on Shopify's CDN.
     */
    @Json(name = "src")
    val url: String,

    /**
     * The width of the image in pixels.
     */
    @Json(name = "width")
    val width: Int,

    /**
     * The height of the image in pixels.
     */
    @Json(name = "height")
    val height: Int
)