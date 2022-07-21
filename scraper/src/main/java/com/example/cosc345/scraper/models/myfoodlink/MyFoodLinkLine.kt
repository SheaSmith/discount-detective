package com.example.cosc345.scraper.models.myfoodlink

import pl.droidsonroids.jspoon.annotation.Selector

data class MyFoodLinkLine(
    @Selector(".ln__name > span")
    val name: String,

    @Selector(".item-per-unit-cost", regex = "\\\$([\\d.]+)")
    val price: Double,

    @Selector(".comparison_price", regex = "\\\$([\\d.]+)")
    val unitPrice: String?,

    @Selector(".comparison_price", regex = "(\\d+[A-z]+)")
    val unitPriceUnit: String?,

    @Selector(".saving-amount", regex = "\\\$([\\d.]+)")
    val savingsDollars: Double?,

    @Selector(".saving-amount", regex = "(\\d+)c")
    val savingsCents: Int?,

    @Selector(".ln__img > img", attr = "src")
    val image: String
)
