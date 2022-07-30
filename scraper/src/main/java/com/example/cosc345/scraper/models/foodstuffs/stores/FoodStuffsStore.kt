package com.example.cosc345.scraper.models.foodstuffs.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A store that exists for a particular FoodStuffs retailer.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsStore(
    /**
     * The unique ID of the store.
     */
    @Json(name = "id")
    val id: String,

    /**
     * The name of the store.
     */
    @Json(name = "name")
    val name: String,

    /**
     * The physical address of the store.
     */
    @Json(name = "address")
    val address: String,

    /**
     * The latitude of the store.
     */
    @Json(name = "latitude")
    val latitude: Double,

    /**
     * The longitude of the store.
     */
    @Json(name = "longitude")
    val longitude: Double,
) {
    /**
     * Get a version of the ID that doesn't include dashes, for getting the prices.
     */
    val idWithoutDashes get() = id.replace("-", "")
}
