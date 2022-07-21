package com.example.cosc345.scraper.models.countdown.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountdownSiteDetail(
    @Json(name = "site")
    val site: CountdownSite
)
