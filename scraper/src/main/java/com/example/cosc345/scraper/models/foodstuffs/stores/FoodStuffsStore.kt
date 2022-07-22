package com.example.cosc345.scraper.models.foodstuffs.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsStore(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "address")
    val address: String,

    @Json(name = "latitude")
    val latitude: Double,

    @Json(name = "longitude")
    val longitude: Double,
) {
    val idWithoutDashes get() = id.replace("-", "")
}
