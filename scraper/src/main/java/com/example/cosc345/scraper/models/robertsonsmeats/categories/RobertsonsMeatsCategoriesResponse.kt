package com.example.cosc345.scraper.models.robertsonsmeats.categories

import pl.droidsonroids.jspoon.annotation.Selector

data class RobertsonsMeatsCategoriesResponse(
    @Selector(".nav > .inner > ul > li")
    var categories: List<RobertsonsMeatsCategory>? = null
)
