package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The object which contains the list of products, along with noting the total items.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownProducts(
    /**
     * The list of products returned in this request.
     */
    @Json(name = "items")
    val items: Array<CountdownProduct>,

    /**
     * The total items for this request, this is irrespective of any paging.
     */
    @Json(name = "totalItems")
    val totalItems: Int,
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CountdownProducts

        if (!items.contentEquals(other.items)) return false
        if (totalItems != other.totalItems) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = items.contentHashCode()
        result = 31 * result + totalItems
        return result
    }
}
