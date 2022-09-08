package com.example.cosc345.scraper.models.robertsonsmeats.categories

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * The individual category for Robertsons Meats.
 *
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class RobertsonsMeatsCategory(
    /**
     * The relative URL for this category.
     */
    @Selector("a", attr = "href")
    var href: String? = null
)
