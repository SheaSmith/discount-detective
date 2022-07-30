package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A specific product at The Warehouse.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class WarehouseProduct(
    /**
     * The unique ID of the product.
     */
    @Json(name = "productId")
    val id: String,

    /**
     * The name of the product.
     */
    @Json(name = "productName")
    val name: String,

    /**
     * The image URL for this product.
     */
    @Json(name = "productImageUrl")
    val imageUrl: String,

    /**
     * The barcode for this product.
     */
    @Json(name = "productBarcode")
    val barcode: String,

    /**
     * The brand for this product, for example, Sanitarium.
     */
    @Json(name = "brandDescription")
    val brand: String,

    /**
     * The price information for the product.
     */
    @Json(name = "priceInfo")
    val priceInfo: WarehouseProductPriceInfo,

    /**
     * The inventory of this specific product (whether it is in stock at this store or not).
     */
    @Json(name = "inventory")
    val inventory: WarehouseProductInventory,

    /**
     * A list of the active promotions for this product.
     */
    @Json(name = "promotions")
    val promotions: Array<WarehouseProductPromotion>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WarehouseProduct

        if (id != other.id) return false
        if (name != other.name) return false
        if (imageUrl != other.imageUrl) return false
        if (barcode != other.barcode) return false
        if (brand != other.brand) return false
        if (priceInfo != other.priceInfo) return false
        if (inventory != other.inventory) return false
        if (!promotions.contentEquals(other.promotions)) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + barcode.hashCode()
        result = 31 * result + brand.hashCode()
        result = 31 * result + priceInfo.hashCode()
        result = 31 * result + inventory.hashCode()
        result = 31 * result + promotions.contentHashCode()
        return result
    }
}
