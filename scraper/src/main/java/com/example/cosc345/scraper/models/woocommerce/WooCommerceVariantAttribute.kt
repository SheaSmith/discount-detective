package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json

/**
 * A variant attribute, which specifies a particular customisable attribute for a product.
 *
 * @author William Hadden
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
data class WooCommerceVariantAttribute(
    /**
     * The name of the attribute.
     */
    @Json(name = "name")
    val id: String?,

    /**
     * The value of the attribute.
     */
    @Json(name = "value")
    val value: String?
)