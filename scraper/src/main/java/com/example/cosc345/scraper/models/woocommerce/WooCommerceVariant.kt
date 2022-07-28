package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WooCommerceVariant(
    @Json(name = "id")
    val id: String?,

    @Json(name = "attributes")
    val attributes: List<WooCommerceVariantAttribute>

)
