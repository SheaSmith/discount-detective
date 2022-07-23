package com.example.cosc345.scraper.models.veggieboys

import pl.droidsonroids.jspoon.annotation.Selector

data class VeggieBoysProduct(
    @Selector("h5")
    var name: String? = null,

    @Selector(".product-price", regex = "\\\$([\\d.]+)")
    var price: Double? = null,

    @Selector(".mediaholder > a > img", attr = "src")
    var imagePath: String? = null,

    @Selector("a", attr = "href", regex = "(\\d+)")
    var id: String? = null,

    @Selector("a", attr = "href", regex = "\\/product\\/(.+)")
    var href: String? = null,

    @Selector("corner-ribbon")
    var onSpecial: String? = null
)
