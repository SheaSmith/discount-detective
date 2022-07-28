package com.example.cosc345.scraper.models.madbutcher

import pl.droidsonroids.jspoon.annotation.Selector

data class MadButcherProduct(
    @Selector(".woocommerce-LoopProduct-link", attr = "href")
    var href: String? = null,

    @Selector(".price")
    var price: String? = null
)
