package com.example.cosc345.scraper.models.veggieboys

import pl.droidsonroids.jspoon.annotation.Selector

data class VeggieBoysProductDetail(
    @Selector(".product-price > small")
    var perKg: String? = null
)
