package com.example.cosc345.scraper.models.foodstuffs.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response that is returned by the get stores request.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsStoresResponse(
    /**
     * The list of stores for this retailer.
     */
    @Json(name = "stores")
    val stores: Array<FoodStuffsStore>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsStoresResponse

        if (!stores.contentEquals(other.stores)) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        return stores.contentHashCode()
    }
}
