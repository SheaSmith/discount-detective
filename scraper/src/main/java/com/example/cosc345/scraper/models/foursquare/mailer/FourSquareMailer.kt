package com.example.cosc345.scraper.models.foursquare.mailer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FourSquareMailer(
    @Json(name = "nid_1")
    val id: String
)
