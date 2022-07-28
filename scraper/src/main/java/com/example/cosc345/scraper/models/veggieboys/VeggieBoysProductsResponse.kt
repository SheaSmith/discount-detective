package com.example.cosc345.scraper.models.veggieboys

import pl.droidsonroids.jspoon.annotation.Selector

data class VeggieBoysProductsResponse(
    @Selector(".product")
    var products: List<VeggieBoysProduct>? = null
)
