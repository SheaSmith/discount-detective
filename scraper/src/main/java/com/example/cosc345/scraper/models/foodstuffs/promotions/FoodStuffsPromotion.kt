package com.example.cosc345.scraper.models.foodstuffs.promotions

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A promotion that exists on a particular product.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsPromotion(
    /**
     * The ID of the product that has the promotion is being run on.
     */
    @Json(name = "productId")
    val productId: String?,

    /**
     * The price on promotion of this product.
     */
    @Json(name = "price")
    val price: Double,

    /**
     * The quantity of this product that is being promoted, this is usually 1 for a standard promotion, or higher for a multi-buy discount (for example, 2 for $5).
     */
    @Json(name = "quantity")
    val quantity: Int,

    /**
     * A promotion that is only available to club members.
     */
    @Json(name = "loyalty")
    val loyaltyPromotion: FoodStuffsPromotion?
)