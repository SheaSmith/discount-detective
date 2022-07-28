package com.example.cosc345.scraper.models.countdown.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Information about a particular Countdown store.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownSite(
    /**
     * The name of the store.
     */
    @Json(name = "name")
    val name: String,

    /**
     * The first line of the address, e.g. 144 Example St
     */
    @Json(name = "addressLine1")
    val addressLine1: String,

    /**
     * The second line of the address. This is optional.
     */
    @Json(name = "addressLine2")
    val addressLine2: String?,

    /**
     * The suburb the store is in. In practice this seems to actually be the town the store is in, for example Dunedin.
     */
    @Json(name = "suburb")
    val suburb: String,

    /**
     * The postcode of the store, for example, 9016.
     */
    @Json(name = "postcode")
    val postcode: String,

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
     * The unique ID for this store, specifically this is used for setting the active store.
     */
    @Json(name = "extra2")
    val storeId: String
)
