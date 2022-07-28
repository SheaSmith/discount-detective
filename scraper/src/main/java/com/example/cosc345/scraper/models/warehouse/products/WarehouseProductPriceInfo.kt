package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Information about a price for a particular product.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseProductPriceInfo(
    /**
     * The price of the product.
     */
    @Json(name = "price")
    val price: Double,

    /**
     * If the product is on promotion, then this will contain the date it comes off promotion.
     */
    @Json(name = "revertDate")
    val date: String?
)
