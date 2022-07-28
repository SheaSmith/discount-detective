package com.example.cosc345.scraper.models.shopify

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response from the get products request from Shopify.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class ShopifyResponse(
    /**
     * The list of products.
     */
    @Json(name = "products")
    val products: Array<ShopifyProduct>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShopifyResponse

        if (!products.contentEquals(other.products)) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        return products.contentHashCode()
    }
}
