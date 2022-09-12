package com.example.cosc345.scraper.models.warehouse.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A specific Warehouse store.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseStore(
    /**
     * The unique ID for this store.
     */
    @Json(name = "branchId")
    val branchId: String,

    /**
     * The name of this store, not including the "The Warehouse" part, for example "South Dunedin".
     */
    @Json(name = "name")
    val name: String,

    /**
     * The latitude of the store.
     */
    @Json(name = "gpsLatitude")
    val latitude: Double,

    /**
     * The longitude of the store.
     */
    @Json(name = "gpsLongitude")
    val longitude: Double,

    /**
     * The name of the region it is in.
     */
    @Json(name = "regionDescription")
    val regionDescription: String,

    /**
     * The physical address of the store.
     */
    @Json(name = "address")
    val address: WarehouseStoreAddress
)
