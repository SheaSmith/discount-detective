package com.example.cosc345.scraper.models.robertsonsmeats.categories

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * The response for a request to get the categories for Robertsons Meats.
 *
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class RobertsonsMeatsCategoriesResponse(
    /**
     * The list of categories that contain the individual products.
     */
    @Selector(".nav > .inner > ul > li")
    var categories: List<RobertsonsMeatsCategory>? = null
)
