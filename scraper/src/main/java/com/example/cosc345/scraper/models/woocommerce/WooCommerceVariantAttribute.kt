package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json

data class WooCommerceVariantAttribute(
    @Json(name = "name")
    val id: String?,

    @Json(name = "value")
    val value: String?
)