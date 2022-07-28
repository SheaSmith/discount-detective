package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A tag on a product specifying extra information.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownProductTag(
    /**
     * Information for multi-buy discounts, for example if you can buy 2 for $5.
     */
    @Json(name = "multiBuy")
    val multiBuy: CountdownProductMultiBuy?
)