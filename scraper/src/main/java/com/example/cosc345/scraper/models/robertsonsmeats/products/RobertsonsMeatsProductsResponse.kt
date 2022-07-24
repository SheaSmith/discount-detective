package com.example.cosc345.scraper.models.robertsonsmeats.products

import pl.droidsonroids.jspoon.annotation.Selector

data class RobertsonsMeatsProductsResponse(
    @Selector(".items > li")
    var products: List<RobertsonsMeatsProduct>? = null
)
