package com.example.cosc345.scraper.models.myfoodlink.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The analytics data for MyFoodLink, which we use to extract more information about products.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class MyFoodLinkGtmData(
    /**
     * The type of event, we look for productListImpression.
     */
    @Json(name = "event")
    val eventType: String,

    /**
     * The data for the products.
     */
    @Json(name = "ecommerce")
    val ecommerce: MyFoodLinkGtmEcommerce
)
