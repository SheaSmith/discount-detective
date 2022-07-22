package com.example.cosc345.scraper.models.foodstuffs.promotions

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsPromotionResponse(
    @Json(name = "promotions")
    val promotions: Array<FoodStuffsPromotion>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsPromotionResponse

        if (!promotions.contentEquals(other.promotions)) return false

        return true
    }

    override fun hashCode(): Int {
        return promotions.contentHashCode()
    }
}
