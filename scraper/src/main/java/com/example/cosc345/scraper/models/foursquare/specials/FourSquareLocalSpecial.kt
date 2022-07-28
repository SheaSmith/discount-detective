package com.example.cosc345.scraper.models.foursquare.specials

import pl.droidsonroids.jspoon.annotation.Selector

data class FourSquareLocalSpecial(
    @Selector(".sxa-specials-card__heading")
    var name: String? = null,

    @Selector(".image-removed-sizes", attr = "src")
    var imagePath: String? = null,

    @Selector(".sxa-specials-card__pricing-dollars", regex = "\\\$?(\\d+)")
    var dollars: Int? = null,

    @Selector(".sxa-specials-card__pricing-cents", regex = "(\\d+)")
    var cents: Int? = null,

    @Selector(".sxa-specials-card__pricing-unit")
    var saleType: String? = null
)
