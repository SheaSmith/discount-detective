package com.example.cosc345.scraper.models.shopify

import com.squareup.moshi.Json

/**
 * An object with metadata about the image, along with the URL.
 */
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