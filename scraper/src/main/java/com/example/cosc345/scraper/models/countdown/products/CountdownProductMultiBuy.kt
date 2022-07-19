package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json

data class CountdownProductMultiBuy(
    @Json(name = "quantity")
    val quantity: Int,

    @Json(name = "value")
    val price: Double
)
