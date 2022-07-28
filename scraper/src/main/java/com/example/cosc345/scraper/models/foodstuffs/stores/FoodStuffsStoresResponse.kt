package com.example.cosc345.scraper.models.foodstuffs.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsStoresResponse(
    @Json(name = "stores")
    val stores: Array<FoodStuffsStore>
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsStoresResponse

        if (!stores.contentEquals(other.stores)) return false

        return true
    }

    override fun hashCode(): Int {
        return stores.contentHashCode()
    }
}
