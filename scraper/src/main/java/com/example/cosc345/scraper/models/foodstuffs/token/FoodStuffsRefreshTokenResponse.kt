package com.example.cosc345.scraper.models.foodstuffs.token

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsRefreshTokenResponse(
    @Json(name = "accessToken")
    val accessToken: String
)
