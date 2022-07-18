package com.example.cosc345.scraper.models.shopify

import com.squareup.moshi.Json

data class ShopifyProduct(
    @Json(name = "id")
    val id: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "vendor")
    val vendor: String,
    @Json(name = "product_type")
    val productType: String,
    @Json(name = "variants")
    val variants: List<ShopifyVariant>,
    @Json(name = "images")
    val images: List<ShopifyImage>,
)
