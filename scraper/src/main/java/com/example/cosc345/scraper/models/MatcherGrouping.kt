package com.example.cosc345.scraper.models

import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType

data class MatcherGrouping(
    val brand: List<String>?,
    val name: List<String>,
    val variant: List<String>?,
    val saleType: SaleType,
    val quantity: String?
) {
    constructor(productInfo: RetailerProductInformation) : this(
        productInfo.brandName?.split(" "),
        productInfo.name!!.split(" "),
        productInfo.variant?.split(" "),
        productInfo.saleType!!,
        productInfo.quantity
    )

    override fun equals(other: Any?): Boolean {
        if (other is MatcherGrouping) {
            if (doesMatch(other) && other.saleType == saleType) {
                return true
            }

            return false
        }
        return super.equals(other)
    }

    private fun doesMatch(other: MatcherGrouping): Boolean {
        var combined1 = name.toSet()
        var combined2 = other.name.toSet()

        if (brand != null)
            combined1 = combined1.union(brand)

        if (other.brand != null)
            combined2 = combined2.union(other.brand)

        val intersection = combined1.intersect(combined2)

        if (combined1.size == intersection.size) {
            return true
        }

        if (other.variant != null && combined2.union(other.variant)
                .intersect(combined1).size == combined1.size
        ) {
            return true
        }

        if (variant != null && combined1.union(variant)
                .intersect(combined2).size == combined2.size
        ) {
            return true
        }

        if (variant != null && other.variant != null && combined1.union(variant)
                .intersect(combined2.union(other.variant)) != combined2.union(other.variant)
        ) {
            return true
        }

        return false
    }

    override fun hashCode(): Int {
        var result = brand?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (variant?.hashCode() ?: 0)
        result = 31 * result + saleType.hashCode()
        result = 31 * result + (quantity?.hashCode() ?: 0)
        return result
    }
}
