package com.example.cosc345.scraper.models.madbutcher

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * The response to the request to get all Mad Butcher products, to verify if they are sold by weight or not.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class MadButcherProductsListResponse(
    /**
     * The list of products.
     */
    @Selector(".product")
    var products: List<MadButcherProduct>? = null,

    /**
     * The maximum page that we can request.
     */
    @Selector(".page-numbers :nth-last-child(2) > a")
    var lastPage: Int? = null
)
