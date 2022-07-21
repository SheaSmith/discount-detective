package com.example.cosc345.scraper.models.myfoodlink

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyFoodLinkGtmData(
    @Json(name = "event")
    val eventType: String


)
