package com.example.cosc345.scraper.models.myfoodlink.products

import pl.droidsonroids.jspoon.annotation.Selector

data class MyFoodLinkLine(
    @Selector(".ln__name > span")
    var name: String? = null,

    @Selector(".item-per-unit-cost", regex = "\\\$([\\d.]+)")
    var price: Double? = null,

    @Selector(".comparison_price", regex = "\\\$([\\d.]+)")
    var unitPrice: String? = null,

    @Selector(".comparison_price", regex = "(\\d+[A-z]+)")
    var unitPriceUnit: String? = null,

    @Selector(".saving-amount", regex = "\\\$([\\d.]+)")
    var savingsDollars: String? = null,

    @Selector(".saving-amount", regex = "(\\d+)c")
    var savingsCents: String? = null,

    @Selector(".ln__img > img", attr = "src")
    var image: String? = null
)
