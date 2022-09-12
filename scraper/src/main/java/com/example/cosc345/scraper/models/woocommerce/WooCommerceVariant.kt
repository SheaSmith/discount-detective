package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A variant for this product, basically a different version of it that can be selected.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WooCommerceVariant(
    /**
     * The ID of this variant.
     */
    @Json(name = "id")
    val id: String?,

    /**
     * The list of attributes for this variant.
     */
    @Json(name = "attributes")
    val attributes: List<WooCommerceVariantAttribute>

)
