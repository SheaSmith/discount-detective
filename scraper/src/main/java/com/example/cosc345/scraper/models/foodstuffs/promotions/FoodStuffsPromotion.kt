package com.example.cosc345.scraper.models.foodstuffs.promotions

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsPromotion(
    @Json(name = "productId")
    val productId: String,

    @Json(name = "price")
    val price: Double,

    @Json(name = "quantity")
    val quantity: Int,

    @Json(name = "loyalty")
    val loyaltyPromotion: FoodStuffsPromotion?
)