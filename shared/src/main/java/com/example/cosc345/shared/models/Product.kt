package com.example.cosc345.shared.models

/**
 * The grouping object which contains the retailer specific product information.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. Some of the nullable parameters are not nullable in practice, but are required to be for Firebase.
 */
data class Product(
    /**
     * The list of information from different retailers for this product.
     */
    var information: MutableList<RetailerProductInformation>? = null,
)
