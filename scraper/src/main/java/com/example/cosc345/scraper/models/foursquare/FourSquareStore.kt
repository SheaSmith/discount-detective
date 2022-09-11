package com.example.cosc345.scraper.models.foursquare

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A particular Four Square store.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FourSquareStore(
    /**
     * The unique ID of this store.
     */
    @Json(name = "id")
    val id: String,

    /**
     * The name of this store.
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

    /**
     * The region code for this particular store, for example, SI for the South Island, or UNI for the Upper North Island.
     */
    @Json(name = "regionCode")
    val region: String
)
