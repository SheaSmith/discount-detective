package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A response to the get products request.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseProductsResponse(
    /**
     * The list of products.
     */
    @Json(name = "products")
    val products: Array<WarehouseProduct>,

    /**
     * The total amount of products captured by this query.
     */
    @Json(name = "total")
    val total: Int
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WarehouseProductsResponse

        if (!products.contentEquals(other.products)) return false
        if (total != other.total) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = products.contentHashCode()
        result = 31 * result + total
        return result
    }
}
