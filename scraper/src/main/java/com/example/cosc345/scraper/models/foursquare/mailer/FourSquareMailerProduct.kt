package com.example.cosc345.scraper.models.foursquare.mailer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A product in the mailer.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FourSquareMailerProduct(
    /**
     * The type of selectable element, we're interested in the product type, but there are others (for example, links for social media).
     */
    @Json(name = "field_region_type")
    val type: String,

    /**
     * The name of the product.
     */
    @Json(name = "field_product_title")
    val name: String,

    /**
     * The price of the product, including price.
     */
    @Json(name = "field_product_price")
    val price: String,

    /**
     * The sale type of this product, for example kg for per-kg pricing.
     */
    @Json(name = "field_price_definitions")
    val saleType: String,

    /**
     * The image for this product.
     */
    @Json(name = "field_product_image_url")
    val imageUrls: Array<String>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FourSquareMailerProduct

        if (name != other.name) return false
        if (price != other.price) return false
        if (saleType != other.saleType) return false
        if (!imageUrls.contentEquals(other.imageUrls)) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + saleType.hashCode()
        result = 31 * result + imageUrls.contentHashCode()
        return result
    }
}
