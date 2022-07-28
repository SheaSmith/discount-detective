package com.example.cosc345.scraper.models.wixstores.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WixStoresProductsResponse(
    @Json(name = "data")
    val data: WixStoresProductsData
)
