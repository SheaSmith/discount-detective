package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json

data class CountdownProduct(
    @Json(name = "type")
    val type: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "barcode")
    val barcode: String?,

    @Json(name = "variety")
    val variety: String?,

    @Json(name = "brand")
    val brand: String?,

    @Json(name = "sku")
    val sku: String?,

    @Json(name = "unit")
    val unit: String?,

    @Json(name = "price")
    val price: CountdownProductPrice?,

    @Json(name = "images")
    val images: CountdownProductImages?,

    @Json(name = "averageWeightPerUnit")
    val averageWeightPerUnit: Double?,

    @Json(name = "size")
    val size: CountdownProductSize?,

    @Json(name = "productTag")
    val productTag: CountdownProductTag?
)
