package com.example.cosc345.scraper.models.warehouse.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WarehouseStoresResponse(
    @Json(name = "branches")
    val stores: Array<WarehouseStore>
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WarehouseStoresResponse

        if (!stores.contentEquals(other.stores)) return false

        return true
    }

    override fun hashCode(): Int {
        return stores.contentHashCode()
    }
}
