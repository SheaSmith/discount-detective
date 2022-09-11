package com.example.cosc345.scraper.models.warehouse.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The object which contains the store address.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseStoreAddress(
    /**
     * The physical address of the store.
     */
    @Json(name = "fullAddress")
    val address: String
)
