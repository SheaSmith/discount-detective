package com.example.cosc345.scraper.models.countdown.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The main product model, defining the attributes of a particular product.
 *
 * @constructor Create an instance of this object. This should only be used by the automatic Moshi parser.
 */
@JsonClass(generateAdapter = true)
data class CountdownProduct(
    /**
     * The type of this item.
     */
    @Json(name = "type")
    val type: String,

    /**
     * The name of the item, all in lowercase.
     */
    @Json(name = "name")
    val name: String,

    /**
     * The single barcode that represents this product.
     */
    @Json(name = "barcode")
    val barcode: String?,

    /**
     * The variety of the product, for example, the colour or the variant, so "red" or "cranberry".
     */
    @Json(name = "variety")
    val variety: String?,

    /**
     * The brand of the product, for example, Essentials
     */
    @Json(name = "brand")
    val brand: String?,

    /**
     * The unique identification number for this product.
     */
    @Json(name = "sku")
    val sku: String?,

    /**
     * The unit used to measure how the product is sold. For example, if this equals "Kg", the product is sold per-kg.
     */
    @Json(name = "unit")
    val unit: String?,

    /**
     * The price for this product.
     */
    @Json(name = "price")
    val price: CountdownProductPrice?,

    /**
     * The image for this product.
     */
    @Json(name = "images")
    val image: CountdownProductImage?,

    /**
     * The average weight per unit of this product. For example, a banana, while generally sold per-kg, can also be sold individually, so this estimates how much each of those bananas would weigh.
     */
    @Json(name = "averageWeightPerUnit")
    val averageWeightPerUnit: Double?,

    /**
     * An object which contains information about the size of this product.
     */
    @Json(name = "size")
    val size: CountdownProductSize?,

    /**
     * Tags associated with this product, for example, multi-buy.
     */
    @Json(name = "productTag")
    val productTag: CountdownProductTag?
)
