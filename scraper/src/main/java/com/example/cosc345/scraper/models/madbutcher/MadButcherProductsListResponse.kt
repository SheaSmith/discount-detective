package com.example.cosc345.scraper.models.madbutcher

import pl.droidsonroids.jspoon.annotation.Selector

data class MadButcherProductsListResponse(
    @Selector(".product")
    var products: List<MadButcherProduct>? = null,

    @Selector(".page-numbers :nth-last-child(2) > a")
    var lastPage: Int? = null
)
