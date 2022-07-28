package com.example.cosc345.scraper.models.myfoodlink.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyFoodLinkGtmEcommerce(
    @Json(name = "impressions")
    val impressions: Array<MyFoodLinkGtmImpression>?
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyFoodLinkGtmEcommerce

        if (!impressions.contentEquals(other.impressions)) return false

        return true
    }

    override fun hashCode(): Int {
        return impressions.contentHashCode()
    }
}
