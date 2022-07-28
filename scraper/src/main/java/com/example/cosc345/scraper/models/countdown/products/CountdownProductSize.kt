package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountdownProductSize(
    @Json(name = "cupPrice")
    val unitPrice: Double,

    @Json(name = "cupMeasure")
    val unitMeasure: String?,

    @Json(name = "volumeSize")
    val size: String?
)
