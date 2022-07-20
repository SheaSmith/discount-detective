package com.example.cosc345.scraper.models.countdown.products

import com.example.cosc345.shared.models.Product
import com.squareup.moshi.Json

data class CountdownProducts(
    @Json(name = "items")
    val items: Array<CountdownProduct>,

    @Json(name = "totalItems")
    val totalItems: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CountdownProducts

        if (!items.contentEquals(other.items)) return false
        if (totalItems != other.totalItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = items.contentHashCode()
        result = 31 * result + totalItems
        return result
    }
}
