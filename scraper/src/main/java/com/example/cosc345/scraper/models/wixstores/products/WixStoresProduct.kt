package com.example.cosc345.scraper.models.wixstores.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A WixStores product.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WixStoresProduct(
    /**
     * The name of the product.
     */
    @Json(name = "name")
    val name: String,

    /**
     * The price of the product.
     */
    @Json(name = "price")
    val price: Double,

    /**
     * Whether the product is in stock or not.
     */
    @Json(name = "isInStock")
    val isInStock: Boolean,

    /**
     * A list of images for this product.
     */
    @Json(name = "media")
    val media: Array<WixStoresImage>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WixStoresProduct

        if (name != other.name) return false
        if (price != other.price) return false
        if (isInStock != other.isInStock) return false
        if (!media.contentEquals(other.media)) return false

        return true
    }

    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + isInStock.hashCode()
        result = 31 * result + media.contentHashCode()
        return result
    }
}
