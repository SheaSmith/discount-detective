package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WarehouseProductsResponse(
    @Json(name = "products")
    val products: Array<WarehouseProduct>,

    @Json(name = "total")
    val total: Int
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WarehouseProductsResponse

        if (!products.contentEquals(other.products)) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = products.contentHashCode()
        result = 31 * result + total
        return result
    }
}
