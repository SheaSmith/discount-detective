package com.example.cosc345.scraper.models.warehouse.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response to a get stores request.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseStoresResponse(
    /**
     * The list of stores.
     */
    @Json(name = "branches")
    val stores: Array<WarehouseStore>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WarehouseStoresResponse

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
