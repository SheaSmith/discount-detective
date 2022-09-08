package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response returned by the get Countdown products request.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownProductsResponse(
    /**
     * An object containing the list of products.
     */
    @Json(name = "products")
    val products: CountdownProducts
)
