package com.example.cosc345.scraper.models.warehouse.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WarehouseProduct(
    @Json(name = "productId")
    val id: String,

    @Json(name = "productName")
    val name: String,

    @Json(name = "productImageUrl")
    val imageUrl: String,

    @Json(name = "productBarcode")
    val barcode: String,

    @Json(name = "brandDescription")
    val brand: String,

    @Json(name = "priceInfo")
    val priceInfo: WarehouseProductPriceInfo,

    @Json(name = "inventory")
    val inventory: WarehouseProductInventory,

    @Json(name = "promotions")
    val promotions: Array<WarehouseProductPromotion>
) {
    // Kotlin boilerplate
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
