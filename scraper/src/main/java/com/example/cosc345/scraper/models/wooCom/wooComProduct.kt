package com.example.cosc345.scraper.models.wooCom

import com.example.cosc345.scraper.models.shopify.ShopifyImage
import com.example.cosc345.scraper.models.shopify.ShopifyVariant
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class wooComProduct(
    @Json(name = "id")
    val id: String?,

    @Json(name = "name")
    val name: String?,

    @Json(name = "type")
    val type: String?,

    @Json(name = "prices")
    val prices: wooComPrice,

    @Json(name = "images")
    val images: List<wooComImage>,

    @Json(name = "categories")
    val categories: List<wooComCategories>,

    /**
     * Need in order to get weights
     */
    @Json(name = "permalink")
    val permaLink: String,

    @Json(name = "variations")
    val variants: List<wooComVariants>,

    @Json(name = "attributes")
    val attributes: List<wooComVariants.wooComAttributes>
)

