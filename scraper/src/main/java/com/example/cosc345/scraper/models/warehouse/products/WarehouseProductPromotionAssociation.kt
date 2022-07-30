package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An associated promotion. This can either be standard, or it can be multi-buy.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseProductPromotionAssociation(
    /**
     * The quantity for the promotion, for example in a 2 for $5 discount, then this will be 2.
     */
    @Json(name = "quantity1")
    val quantity1: Int,

    /**
     * The quantity for some multi-buy promotions. For example, get 1 and get another half price, this will be 1.
     */
    @Json(name = "quantity2")
    val quantity2: Int?,

    /**
     * The total amount for the promotion. So for get 2 for $5, this will be 5, but for get one and get another half price, this will be the total price (so the original price * 1.5).
     */
    @Json(name = "amount")
    val amount: Double
)
