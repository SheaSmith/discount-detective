package com.example.cosc345.scraper.models.foodstuffs.products

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsProduct(
    @Json(name = "barcodes")
    val barcodes: String,

    @Json(name = "category1")
    val categories: Array<String>,

    @Json(name = "netContent")
    val netContent: String?,

    @Json(name = "netContentDisplay")
    val netContentDisplay: String?,

    @Json(name = "netContentUOM")
    val netContentUnit: String?,

    @Json(name = "prices")
    val prices: Map<String, String>,

    @Json(name = "productID")
    val productId: String,

    @Json(name = "promotionStart")
    val promotionStart: Map<String, String>,

    @Json(name = "promotionEnd")
    val promotionEnd: Map<String, String>,

    @Json(name = "saleType")
    val saleType: String,

    @Json(name = "DisplayName")
    val name: String
) {
    // Kotlin boilerplate
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