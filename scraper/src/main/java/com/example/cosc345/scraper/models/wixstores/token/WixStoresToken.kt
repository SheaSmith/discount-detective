package com.example.cosc345.scraper.models.wixstores.token

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The object which contains the access token needed to interact with the WixStores API.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WixStoresToken(
    /**
     * The access token needed for authentication.
     */
    @Json(name = "instance")
    val token: String
)
