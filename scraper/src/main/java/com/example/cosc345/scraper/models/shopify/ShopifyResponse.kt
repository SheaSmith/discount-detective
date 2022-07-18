package com.example.cosc345.scraper.models.shopify

import com.squareup.moshi.Json

data class ShopifyResponse(
    @Json(name = "products")
    val products: Array<ShopifyProduct>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShopifyResponse

        if (!products.contentEquals(other.products)) return false

        return true
    }

    override fun hashCode(): Int {
        return products.contentHashCode()
    }
}
