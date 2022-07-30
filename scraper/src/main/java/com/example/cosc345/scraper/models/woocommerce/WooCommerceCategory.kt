package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Object to hold category metadata.
 *
 * @author William Hadden
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WooCommerceCategory(

    /**
     * The unique ID of the category.
     */
    @Json(name = "id")
    val id: String?,

    /**
     * The name of the category.
     */
    @Json(name = "name")
    val name: String?
)
