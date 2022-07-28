package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WarehouseProductPromotionAssociation(
    @Json(name = "quantity1")
    val quantity1: Int,

    @Json(name = "quantity2")
    val quantity2: Int?,

    @Json(name = "amount")
    val amount: Double
)
