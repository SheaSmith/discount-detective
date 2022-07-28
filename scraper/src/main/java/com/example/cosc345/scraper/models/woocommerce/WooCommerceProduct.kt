package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WooCommerceProduct(
    @Json(name = "id")
    val id: String?,

    @Json(name = "name")
    val name: String,

    @Json(name = "type")
    val type: String?,

    @Json(name = "on_sale")
    val onSale: Boolean,

    @Json(name = "is_in_stock")
    val inStock: Boolean,

    @Json(name = "prices")
    val prices: WooCommercePrice,

    @Json(name = "images")
    val images: List<WooCommerceImage>,

    @Json(name = "categories")
    val categories: List<WooCommerceCategory>,

    /**
     * Need in order to get weights
     */
    @Json(name = "permalink")
    val permaLink: String,

    @Json(name = "variations")
    val variants: List<WooCommerceVariant>,

    @Json(name = "attributes")
    val attributes: List<WooCommerceAttribute>
)

