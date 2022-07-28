package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specific information about a product promotion.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseProductPromotion(
    /**
     * The promotion price.
     */
    @Json(name = "price")
    val price: Double?,

    /**
     * An associated promotion.
     */
    @Json(name = "association")
    val association: WarehouseProductPromotionAssociation?
)
