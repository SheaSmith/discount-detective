package com.example.cosc345.scraper.models.woocommerce

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A particular product on a WooCommerce store.
 *
 * @author William Hadden
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WooCommerceProduct(
    /**
     * The unique ID of this product.
     */
    @Json(name = "id")
    val id: String?,

    /**
     * The name of this product.
     */
    @Json(name = "name")
    val name: String,

    /**
     * The sale type of this product (for example, variable for products whose price changes based on an attribute).
     */
    @Json(name = "type")
    val type: String?,

    /**
     * Whether the product is on sale or not.
     */
    @Json(name = "on_sale")
    val onSale: Boolean,

    /**
     * Whether the product is in stock or not.
     */
    @Json(name = "is_in_stock")
    val inStock: Boolean,

    /**
     * The price for this product.
     */
    @Json(name = "prices")
    val prices: WooCommercePrice,

    /**
     * The list of images for this product.
     */
    @Json(name = "images")
    val images: List<WooCommerceImage>,

    /**
     * The categories this product belongs to.
     */
    @Json(name = "categories")
    val categories: List<WooCommerceCategory>,

    /**
     * A URL for this particular product.
     */
    @Json(name = "permalink")
    val permaLink: String,

    /**
     * The different variations of this product.
     */
    @Json(name = "variations")
    val variants: List<WooCommerceVariant>,

    /**
     * The different attributes which can be used to price this product.
     */
    @Json(name = "attributes")
    val attributes: List<WooCommerceAttribute>
)

