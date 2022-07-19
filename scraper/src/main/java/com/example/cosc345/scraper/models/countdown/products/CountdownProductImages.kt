package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json

data class CountdownProductImages(
    @Json(name = "big")
    val imageUrl: String
)
