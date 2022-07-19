package com.example.cosc345.scraper.models.countdown

import com.squareup.moshi.Json

data class CountdownSetStoreRequest(
    @Json(name = "addressId")
    val addressId: Int
)
