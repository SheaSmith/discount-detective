package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WooCommerceAttribute(
    @Json(name = "name")
    val name: String,

    @Json(name = "terms")
    val terms: Array<WooCommerceTerm>
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WooCommerceAttribute

        if (name != other.name) return false
        if (!terms.contentEquals(other.terms)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + terms.contentHashCode()
        return result
    }
}
