package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountdownProductPrice(
    @Json(name = "originalPrice")
    val originalPrice: Double,

    @Json(name = "salePrice")
    val salePrice: Double,

    @Json(name = "savePrice")
    val savePrice: Double,

    @Json(name = "isClubPrice")
    val isClubPrice: Boolean
)
