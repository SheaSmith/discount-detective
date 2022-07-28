package com.example.cosc345.scraper.models.countdown

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountdownSetStoreRequest(
    @Json(name = "addressId")
    val addressId: Int
)
