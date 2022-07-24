package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An object containg Metadata about the price
 */
@JsonClass(generateAdapter = true)
data class WooCommercePrice(

    /**
     * Numerical value of the regular price, in cents.
     */
    @Json(name = "regular_price")
    val price: String,

    /**
     * The price of the product when it is on sale, in the number of cents.
     */
    @Json(name = "sale_price")
    val discountPrice: String

)
