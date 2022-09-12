package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A term is a specific value that an attribute can be set to, for example, a specific weight.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WooCommerceTerm(
    /**
     * The name of the value, for example 500gm.
     */
    @Json(name = "name")
    val name: String
)
