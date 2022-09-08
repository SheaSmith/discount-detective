package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The inventory info for this particular product.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseProductInventory(
    /**
     * Whether this product is available at the particular store or not.
     */
    @Json(name = "available")
    val available: Boolean
)
