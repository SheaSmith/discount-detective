package com.example.cosc345.scraper.models.countdown.departments

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountdownDepartment(
    @Json(name = "url")
    val url: String
)
