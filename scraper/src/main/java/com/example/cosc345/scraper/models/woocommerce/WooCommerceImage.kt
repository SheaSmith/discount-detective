package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An object with metadata about the image.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WooCommerceImage(
    /**
     * The image URL.
     */
    @Json(name = "src")
    val url: String

)