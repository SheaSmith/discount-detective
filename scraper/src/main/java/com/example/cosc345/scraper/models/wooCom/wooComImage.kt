package com.example.cosc345.scraper.models.wooCom

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An object with metadata about image, src and url
 */
@JsonClass(generateAdapter = true)
data class wooComImage(
    /**
     * The Image URL
     */
    @Json(name = "src")
    val url: String

)