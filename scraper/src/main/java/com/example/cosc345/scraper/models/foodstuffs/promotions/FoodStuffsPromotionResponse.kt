package com.example.cosc345.scraper.models.foodstuffs.promotions

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response returned by a request to the promotions endpoint.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsPromotionResponse(
    /**
     * A list of promotions.
     */
    @Json(name = "promotions")
    val promotions: Array<FoodStuffsPromotion>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsPromotionResponse

        if (!promotions.contentEquals(other.promotions)) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        return promotions.contentHashCode()
    }
}
