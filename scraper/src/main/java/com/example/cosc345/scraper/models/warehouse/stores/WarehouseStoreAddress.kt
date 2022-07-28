package com.example.cosc345.scraper.models.warehouse.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WarehouseStoreAddress(
    @Json(name = "fullAddress")
    val address: String
)
