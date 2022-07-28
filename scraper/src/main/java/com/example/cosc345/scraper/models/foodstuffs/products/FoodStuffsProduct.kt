package com.example.cosc345.scraper.models.foodstuffs.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A specific FoodStuffs product.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsProduct(
    /**
     * The barcodes associated with this product, separated by commas.
     */
    @Json(name = "barcodes")
    val barcodes: String,

    /**
     * The brand of this product, for example, Pams.
     */
    @Json(name = "brand")
    val brand: String?,

    /**
     * The categories associated with this product, for example, Fruit and Vegetables.
     */
    @Json(name = "category1")
    val categories: Array<String>,

    /**
     * The quantity of the product, not including the unit, for example, 1 for 1kg.
     */
    @Json(name = "netContent")
    val netContent: String?,

    /**
     * The full quantity display, including the number and the unit, for example, 1kg.
     */
    @Json(name = "netContentDisplay")
    val netContentDisplay: String?,

    /**
     * The unit the quantity is measured in, for example kg for 1kg.
     */
    @Json(name = "netContentUOM")
    val netContentUnit: String?,

    /**
     * A map of the prices, with the key being the ID of the store with the dashes remove, and the value being a string representation of the price, with decimals, for example "5.11" for $5.11.
     */
    @Json(name = "prices")
    val prices: Map<String, String>,

    /**
     * The list of stores that stock this product, with the values being the ID of the stores with dashes.
     */
    @Json(name = "stores")
    val stores: List<String>,

    /**
     * The unique ID of this product.
     */
    @Json(name = "productID")
    val productId: String,

    /**
     * A map of when any promotion starts, with the key being the ID of the retailer with dashes, and the value being the Unix timestamp of when the promotion starts.
     */
    @Json(name = "promotionStart")
    val promotionStart: Map<String, String>,

    /**
     * A map of when any promotion ends, with the key being the ID of the retailer with dashes, and the value being the Unix timestamp of when the promotion ends.
     */
    @Json(name = "promotionEnd")
    val promotionEnd: Map<String, String>,

    /**
     * How the product is sold, for example, "WEIGHT" for products sold by weight.
     */
    @Json(name = "saleType")
    val saleType: String,

    /**
     * The name of the product.
     */
    @Json(name = "DisplayName")
    val name: String
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsProduct

        if (barcodes != other.barcodes) return false
        if (!categories.contentEquals(other.categories)) return false
        if (netContent != other.netContent) return false
        if (netContentDisplay != other.netContentDisplay) return false
        if (netContentUnit != other.netContentUnit) return false
        if (prices != other.prices) return false
        if (productId != other.productId) return false
        if (promotionStart != other.promotionStart) return false
        if (promotionEnd != other.promotionEnd) return false
        if (saleType != other.saleType) return false
        if (name != other.name) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = barcodes.hashCode()
        result = 31 * result + categories.contentHashCode()
        result = 31 * result + (netContent?.hashCode() ?: 0)
        result = 31 * result + (netContentDisplay?.hashCode() ?: 0)
        result = 31 * result + (netContentUnit?.hashCode() ?: 0)
        result = 31 * result + prices.hashCode()
        result = 31 * result + productId.hashCode()
        result = 31 * result + promotionStart.hashCode()
        result = 31 * result + promotionEnd.hashCode()
        result = 31 * result + saleType.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}