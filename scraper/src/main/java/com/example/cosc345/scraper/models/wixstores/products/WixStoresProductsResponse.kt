package com.example.cosc345.scraper.models.wixstores.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The response from the get products request.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WixStoresProductsResponse(
    /**
     * The data returned, including the list of products.
     */
    @Json(name = "data")
    val data: WixStoresProductsData
)
