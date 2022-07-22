package com.example.cosc345.scraper.models.foodstuffs.categories

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsCategory(
    @Json(name = "CategoryID")
    val categoryId: String,

    @Json(name = "sitecoreId")
    val id: String,

    @Json(name = "childrenCategories")
    val childrenCategories: Array<String>,

    @Json(name = "level")
    val level: Int
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsCategory

        if (categoryId != other.categoryId) return false
        if (id != other.id) return false
        if (!childrenCategories.contentEquals(other.childrenCategories)) return false
        if (level != other.level) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categoryId.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + childrenCategories.contentHashCode()
        result = 31 * result + level
        return result
    }
}