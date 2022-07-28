package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WarehouseProductPromotion(
    @Json(name = "price")
    val price: Double?,

    @Json(name = "association")
    val association: WarehouseProductPromotionAssociation?
)
