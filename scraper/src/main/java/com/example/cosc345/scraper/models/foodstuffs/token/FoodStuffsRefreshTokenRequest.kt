package com.example.cosc345.scraper.models.foodstuffs.token

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The model for a request that is sent to the get token endpoint.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsRefreshTokenRequest(
    /**
     * The refresh token to use to generate a new access token.
     */
    @Json(name = "refreshToken")
    val refreshToken: String
)
