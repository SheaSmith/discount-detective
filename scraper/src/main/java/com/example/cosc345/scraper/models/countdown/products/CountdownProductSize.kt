package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The object containing information about the product sizing.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownProductSize(
    /**
     * The unit price for goods sold individually. For example, the price per 100g.
     */
    @Json(name = "cupPrice")
    val unitPrice: Double,

    /**
     * The measurement for unit pricing, for example 100g for pricing per 100g.
     */
    @Json(name = "cupMeasure")
    val unitMeasure: String?,

    /**
     * The size of this particular product, for example 400g.
     */
    @Json(name = "volumeSize")
    val size: String?
)
