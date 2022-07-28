package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountdownProductTag(
    @Json(name = "multiBuy")
    val multiBuy: CountdownProductMultiBuy?
)