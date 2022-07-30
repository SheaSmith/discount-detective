package com.example.cosc345.scraper.models.foodstuffs.token

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response returned by the get access token request.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsRefreshTokenResponse(
    /**
     * The access token that is used to authenticate any requests.
     */
    @Json(name = "accessToken")
    val accessToken: String
)
