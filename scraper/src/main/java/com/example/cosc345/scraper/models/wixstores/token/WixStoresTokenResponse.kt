package com.example.cosc345.scraper.models.wixstores.token

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response returned by the get token request.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WixStoresTokenResponse(
    /**
     * A map, with the key being a unique ID for a particular API and the value being an object which contains the access token needed to access it.
     */
    @Json(name = "apps")
    val tokens: Map<String, WixStoresToken>
)
