package com.example.cosc345.scraper.models.myfoodlink.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyFoodLinkGtmImpression(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "price")
    val price: String,

    @Json(name = "brand")
    val brand: String?,

    @Json(name = "dimension4")
    val saleType: String
)
