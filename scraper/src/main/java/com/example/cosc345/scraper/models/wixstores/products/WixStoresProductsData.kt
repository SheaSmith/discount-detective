package com.example.cosc345.scraper.models.wixstores.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WixStoresProductsData(
    @Json(name = "products")
    val products: Array<WixStoresProduct>
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WixStoresProductsData

        if (!products.contentEquals(other.products)) return false

        return true
    }

    override fun hashCode(): Int {
        return products.contentHashCode()
    }
}
