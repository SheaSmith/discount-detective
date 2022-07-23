package com.example.cosc345.scraper.models.warehouse.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WarehouseStore(
    @Json(name = "branchId")
    val branchId: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "gpsLatitude")
    val latitude: Double,

    @Json(name = "gpsLongitude")
    val longitude: Double,

    @Json(name = "regionDescription")
    val regionDescription: String,

    @Json(name = "address")
    val address: WarehouseStoreAddress
)
