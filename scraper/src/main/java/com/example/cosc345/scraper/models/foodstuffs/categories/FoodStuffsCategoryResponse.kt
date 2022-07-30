package com.example.cosc345.scraper.models.foodstuffs.categories

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response for the get categories request.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsCategoryResponse(
    /**
     * The list of categories.
     */
    @Json(name = "hits")
    val categories: Array<FoodStuffsCategory>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsCategoryResponse

        if (!categories.contentEquals(other.categories)) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        return categories.contentHashCode()
    }
}
