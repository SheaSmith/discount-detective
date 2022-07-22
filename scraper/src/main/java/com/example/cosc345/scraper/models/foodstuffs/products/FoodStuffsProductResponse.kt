package com.example.cosc345.scraper.models.foodstuffs.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsProductResponse(
    @Json(name = "hits")
    val products: Array<FoodStuffsProduct>,

    @Json(name = "nbHits")
    val numberOfResults: Int
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsProductResponse

        if (!products.contentEquals(other.products)) return false
        if (numberOfResults != other.numberOfResults) return false

        return true
    }

    override fun hashCode(): Int {
        var result = products.contentHashCode()
        result = 31 * result + numberOfResults
        return result
    }
}
