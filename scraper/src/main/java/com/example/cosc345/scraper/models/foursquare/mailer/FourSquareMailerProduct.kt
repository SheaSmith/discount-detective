package com.example.cosc345.scraper.models.foursquare.mailer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FourSquareMailerProduct(
    @Json(name = "field_region_type")
    val type: String,

    @Json(name = "field_product_title")
    val name: String,

    @Json(name = "field_product_price")
    val price: String,

    @Json(name = "field_price_definitions")
    val saleType: String,

    @Json(name = "field_product_image_url")
    val imageUrls: Array<String>
) {
    // Kotlin boilerplate
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

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + saleType.hashCode()
        result = 31 * result + imageUrls.contentHashCode()
        return result
    }
}
