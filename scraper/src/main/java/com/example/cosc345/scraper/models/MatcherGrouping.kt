package com.example.cosc345.scraper.models

import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType

data class MatcherGrouping(
    val id: String,
    val retailer: String,
    var brand: List<String>?,
    var name: List<String>,
    val variant: List<String>?,
    val saleType: SaleType,
    val quantity: String?,
    val weight: Int?
) {
    constructor(productInfo: RetailerProductInformation) : this(
        productInfo.id!!,
        productInfo.retailer!!,
        productInfo.brandName?.tidy()?.split(" "),
        productInfo.name!!.tidy().split(" "),
        productInfo.variant?.tidy()?.split(" "),
        productInfo.saleType!!,
        productInfo.quantity,
        productInfo.weight
    ) {
        val ignoredWordsForRetailer =
            ignoredWords.filter { it.key.contains(retailer) }.values.firstOrNull()

        if (ignoredWordsForRetailer != null && productInfo.brandName != null) {
            var brandName = productInfo.brandName!!.tidy()
            ignoredWordsForRetailer.forEach {
                if (brandName.startsWith(it, ignoreCase = true)) {
                    brandName = brandName.replaceFirst(it, "", ignoreCase = true)
                        .replace(Regex("\\s+"), " ").trim()
                }
            }

            brand = brandName.split(" ").filter { it.isNotBlank() }

            if (brand?.size == 0)
                brand = null
        }

        if (ignoredWordsForRetailer != null) {
            var newName = productInfo.name!!.tidy()
            ignoredWordsForRetailer.forEach {
                if (newName.startsWith(it, ignoreCase = true)) {
                    newName = newName.replaceFirst(it, "", ignoreCase = true)
                        .replace(Regex("\\s+"), " ").trim()
                }
            }

            name = newName.split(" ").filter { it.isNotBlank() }

            if (name.isEmpty()) {
                name = brand!!
                brand = null
            }
        }
    }

    companion object {
        val quantityRegex = Regex("([\\d.]+)")
        val ignoredWords = mapOf(
            Pair(
                listOf("countdown"),
                listOf(
                    "Fresh Produce",
                    "Countdown",
                    "Macro",
                    "Instore Deli",
                    "Value",
                    "Instore Bakery",
                    "Instore Bakery Artisan",
                    "In Store Bakery",
                    "Signature Range",
                    "Homebrand",
                    "Essentials",
                    "Select"
                )
            ),
            Pair(
                listOf("new-world", "paknsave"),
                listOf("Pams", "Pams Superfoods", "Pams Finest", "Pams Fresh", "Value")
            ),
            Pair(
                listOf("freshchoice", "supervalue"),
                listOf(
                    "WW",
                    "Homebrand",
                    "Essentials",
                    "Countdown",
                    "Macro",
                    "Select",
                    "Signature Range"
                )
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (retailer.contains("leckies", ignoreCase = true)) {
            val i = 0
        }

        if (other is MatcherGrouping) {
            if (doesMatch(other) && other.saleType == saleType) {
                if (quantity != null && other.quantity != null) {
                    if (weight != null && other.weight != null && other.saleType == SaleType.WEIGHT)
                        return true

                    val quantityUnits1 = quantity.replace(quantityRegex, "").replace(" ", "")
                    val quantityUnits2 = other.quantity.replace(quantityRegex, "").replace(" ", "")

                    val numbers1 = mutableListOf<Double>()
                    quantityRegex.findAll(quantity).forEach {
                        val number = it.groups[1]!!.value.toDouble()
                        numbers1.add(number)
                    }

                    val numbers2 = mutableListOf<Double>()
                    quantityRegex.findAll(other.quantity).forEach {
                        val number = it.groups[1]!!.value.toDouble()
                        numbers2.add(number)
                    }

                    return numbers1.containsAll(numbers2) && numbers2.containsAll(numbers1) && quantityUnits1.equals(
                        quantityUnits2,
                        ignoreCase = true
                    )
                } else {
                    return quantity == other.quantity
                }
            }

            return false
        }
        return super.equals(other)
    }

    private fun doesMatch(other: MatcherGrouping): Boolean {
        var combined1 = name.toSet()
        var combined2 = other.name.toSet()

        if (brand != null)
            combined1 = combined1.union(brand!!)

        if (other.brand != null)
            combined2 = combined2.union(other.brand!!)

        val intersection = combined1.containsAll(combined2) && combined2.containsAll(combined1)

        if (intersection) {
            return true
        }

        if (other.variant != null) {
            val newArray = combined2.union(other.variant)
            if (newArray.containsAll(combined1) && combined1.containsAll(newArray)) {
                return true
            }
        }

        if (variant != null) {
            val newArray = combined1.union(variant)
            if (newArray.containsAll(combined2) && combined2.containsAll(newArray)) {
                return true
            }
        }

        if (variant != null && other.variant != null) {
            val newArray = combined1.union(variant)
            val newArray2 = combined2.union(variant)
            if (newArray2.containsAll(newArray) && newArray.containsAll(newArray2)) {
                return true
            }
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

fun String.tidy(): String {
    return this.replace(Regex("[()\\-'\"]"), "").replace("\\s+", " ").trim()
}