package com.example.cosc345.scraper.models.wooCom

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An object containg Metadata about the price
 */
@JsonClass(generateAdapter = true)
data class wooComPrice(

    /**
     * Numerical value of the price
     */
    @Json(name = "price")
    val price: String,

    /**
     * The currency code (e.g NZD)
     */
    @Json(name = "currency_code")
    val currency_code: String,

    /**
     * Determines where the Dp point is:
     * e.g if price = 1250, and currency minor unit = 2
     * then real val is 12.50
     */
    @Json(name = "currency_minor_unit")
    val currency_minor_unit: String,

    /**
     * Gives the prefix (e.g $, pounds etc)
     */
    @Json(name = "currency_prefix")
    val currency_prefix: String

)
