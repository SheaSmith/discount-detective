package com.example.cosc345.scraper.models.wixstores.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WixStoresProduct(
    @Json(name = "name")
    val name: String,

    @Json(name = "price")
    val price: Double,

    @Json(name = "isInStock")
    val isInStock: Boolean,

    @Json(name = "media")
    val media: Array<WixStoresImage>
) {
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

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + isInStock.hashCode()
        result = 31 * result + media.contentHashCode()
        return result
    }
}
