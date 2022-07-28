package com.example.cosc345.scraper.models.robertsonsmeats.products

import pl.droidsonroids.jspoon.annotation.Selector

data class RobertsonsMeatsProduct(
    @Selector("p")
    var name: String? = null,

    @Selector("img", attr = "src")
    var imagePath: String? = null,

    @Selector(".price", regex = "\\$(\\d+)")
    var price: Double? = null,

    @Selector(".weight")
    var weight: String? = null
)
