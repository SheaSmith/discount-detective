package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An object with metadata about image, src and url
 */
@JsonClass(generateAdapter = true)
data class WooCommerceImage(
    /**
     * The Image URL
     */
    @Json(name = "src")
    val url: String

)