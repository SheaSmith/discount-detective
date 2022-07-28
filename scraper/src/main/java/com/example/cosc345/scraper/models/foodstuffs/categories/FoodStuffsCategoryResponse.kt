package com.example.cosc345.scraper.models.foodstuffs.categories

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsCategoryResponse(
    @Json(name = "hits")
    val categories: Array<FoodStuffsCategory>
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsCategoryResponse

        if (!categories.contentEquals(other.categories)) return false

        return true
    }

    override fun hashCode(): Int {
        return categories.contentHashCode()
    }
}
