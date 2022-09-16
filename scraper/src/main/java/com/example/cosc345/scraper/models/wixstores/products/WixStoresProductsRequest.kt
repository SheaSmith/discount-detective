package com.example.cosc345.scraper.models.wixstores.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The request used for getting the products.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WixStoresProductsRequest(
    /**
     * A GraphQL query that returns the expected response. See [WixStoresProductsResponse] for what is expected.
     */
    @Json(name = "query")
    val query: String
)
