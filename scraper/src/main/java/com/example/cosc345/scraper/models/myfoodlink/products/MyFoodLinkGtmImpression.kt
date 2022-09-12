package com.example.cosc345.scraper.models.myfoodlink.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The individual product in the analytics data.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class MyFoodLinkGtmImpression(
    /**
     * The barcode for this product.
     */
    @Json(name = "id")
    val id: String,

    /**
     * The name of this product, which is used to match it to the HTML response.
     */
    @Json(name = "name")
    val name: String,

    /**
     * The price of the product.
     */
    @Json(name = "price")
    val price: String,

    /**
     * The brand of the product, for example, WW.
     */
    @Json(name = "brand")
    val brand: String?,

    /**
     * The sale type of the product, for example, if it is sold per-kg.
     */
    @Json(name = "dimension4")
    val saleType: String
)
