package com.example.cosc345.scraper.models.wixstores.token

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WixStoresToken(
    @Json(name = "instance")
    val token: String
)
