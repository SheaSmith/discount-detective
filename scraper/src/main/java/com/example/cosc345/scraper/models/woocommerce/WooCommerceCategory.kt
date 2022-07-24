package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Object to hold category metadata
 */
@JsonClass(generateAdapter = true)
data class WooCommerceCategory(

    @Json(name = "id")
    val id: String?,

    /**
     * Category name
     */
    @Json(name = "name")
    val name: String?
)
