package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The main product model, defining the attributes of a particular product.
 *
 * @author Shea Smith
 * @constructor Create an instance of this object. This should only be used by the automatic Moshi parser.
 */
@JsonClass(generateAdapter = true)
data class CountdownProduct(
    /**
     * The type of this item.
     */
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
