package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json

data class CountdownProductTag(
    @Json(name = "multiBuy")
    val multiBuy: CountdownProductMultiBuy?
)