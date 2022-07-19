package com.example.cosc345.scraper.models.countdown.departments

import com.squareup.moshi.Json

data class CountdownDepartment(
    @Json(name = "url")
    val url: String
)
