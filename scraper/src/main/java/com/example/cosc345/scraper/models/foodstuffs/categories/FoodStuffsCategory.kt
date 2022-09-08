package com.example.cosc345.scraper.models.foodstuffs.categories

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A category for categorising different products, for example, Fruit and Vegetables.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsCategory(
    /**
     * The ID used for the products query.
     */
    @Json(name = "DisplayName")
    val categoryId: String,

    /**
     * The ID used to cross-reference any child categories.
     */
    @Json(name = "sitecoreId")
    val id: String,

    /**
     * A list of child category IDs.
     */
    @Json(name = "childrenCategories")
    val childrenCategories: Array<String>,

    /**
     * The category level, essentially what depth this category exists at. 1 for top-level categories, 2 for sub-categories, etc.
     */
    @Json(name = "level")
    val level: Int
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
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

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = categoryId.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + childrenCategories.contentHashCode()
        result = 31 * result + level
        return result
    }
}