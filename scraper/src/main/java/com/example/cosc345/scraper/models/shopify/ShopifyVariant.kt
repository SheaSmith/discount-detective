package com.example.cosc345.scraper.models.shopify

import com.squareup.moshi.Json

/**
 * The different product variants, which include the prices.
 */
data class ShopifyVariant(
    /**
     * The name of the variant, for example Steak.
     */
    @Json(name = "title")
    val title: String,

    /**
     * Whether the product is currently available or not.
     */
    @Json(name = "available")
    val available: Boolean,

    /**
     * The price of the variant, represented as a string. For NZ dollars, it is a decimal number with two decimal places.
     */
    @Json(name = "price")
    val price: String,

    /**
     * The weight of the variant in grams. Often this is not set, so we must look at the title of the variant as well to determine weight.
     */
    @Json(name = "grams")
    val weightGrams: Int
)
