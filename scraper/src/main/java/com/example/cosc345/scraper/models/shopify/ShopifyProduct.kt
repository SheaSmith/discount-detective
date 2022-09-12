package com.example.cosc345.scraper.models.shopify

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The individual product from the Shopify store.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class ShopifyProduct(
    /**
     * The unique ID of the product.
     */
    @Json(name = "id")
    val id: String,

    /**
     * The name of the product.
     */
    @Json(name = "title")
    val title: String,

    /**
     * Who sells this product, for example, a specific brand.
     */
    @Json(name = "vendor")
    val vendor: String,

    /**
     * The category of this product.
     */
    @Json(name = "product_type")
    val productType: String,

    /**
     * Any variants that can be used to purchase this product.
     */
    @Json(name = "variants")
    val variants: List<ShopifyVariant>,

    /**
     * The list of images associated with the product.
     */
    @Json(name = "images")
    val images: List<ShopifyImage>,
)
