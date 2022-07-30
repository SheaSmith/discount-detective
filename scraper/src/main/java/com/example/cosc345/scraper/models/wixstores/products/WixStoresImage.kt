package com.example.cosc345.scraper.models.wixstores.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An object containing information about the product image.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WixStoresImage(
    /**
     * The URL of the image.
     */
    @Json(name = "fullUrl")
    val imageUrl: String
)
