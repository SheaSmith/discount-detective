package com.example.cosc345.scraper.models.countdown.stores

import com.squareup.moshi.Json

data class CountdownSiteDetail(
    @Json(name = "site")
    val site: CountdownSite
)
