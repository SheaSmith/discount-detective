package com.example.cosc345.scraper.models.countdown

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The request used to set the active store for pricing purposes.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownSetStoreRequest(
    /**
     * The ID the store to set to active.
     */
    @Json(name = "addressId")
    val addressId: Int
)
