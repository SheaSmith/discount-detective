package com.example.cosc345.scraper.models.foodstuffs.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response from the get products request.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsProductResponse(
    /**
     * A list of the products.
     */
    @Json(name = "hits")
    val products: Array<FoodStuffsProduct>,

    /**
     * The total number of products that match this query, even if that is greater than the 1000 that our request is limited to.
     */
    @Json(name = "nbHits")
    val numberOfResults: Int
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsProductResponse

        if (!products.contentEquals(other.products)) return false
        if (numberOfResults != other.numberOfResults) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = products.contentHashCode()
        result = 31 * result + numberOfResults
        return result
    }
}
