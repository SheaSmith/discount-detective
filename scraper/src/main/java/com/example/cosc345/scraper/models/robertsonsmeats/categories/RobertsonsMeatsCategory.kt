package com.example.cosc345.scraper.models.robertsonsmeats.categories

import pl.droidsonroids.jspoon.annotation.Selector

data class RobertsonsMeatsCategory(
    @Selector("a", attr = "href")
    var href: String? = null
)
