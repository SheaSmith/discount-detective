package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json

data class CountdownProductsResponse(
    @Json(name = "products")
    val products: CountdownProducts
)
