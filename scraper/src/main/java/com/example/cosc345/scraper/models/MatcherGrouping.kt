package com.example.cosc345.scraper.models

import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType

/**
 * A special class which aids in matching products based on their name, irrespective of the order of the words in the product name.
 *
 * @author Shea Smith
 * @constructor Create an instance of the class without needing to supply a product.
 */
data class MatcherGrouping(
    /**
     * The ID of the product.
     */
    val id: String,

    /**
     * The retailer for this particular product.
     */
    val retailer: String,

    /**
     * An array, containing the words in the brand field of the product.
     */
    var brand: List<String>?,

    /**
     * An array, containing the words in the name field of the product.
     */
    var name: List<String>,

    /**
     * An array, containing the words in the variant field of the product.
     */
    val variant: List<String>?,

    /**
     * The sale type of the product (whether it is sold by weight or not).
     */
    val saleType: SaleType,

    /**
     * The quantity of the product.
     */
    val quantity: String?,

    /**
     * The weight of the product, if applicable.
     */
    val weight: Int?
) {
    /**
     * Create a matcher grouping based on a particular product.
     *
     * @param productInfo The information about this product, specific to the retailer.
     */
    constructor(productInfo: RetailerProductInformation) : this(
        productInfo.id!!,
        productInfo.retailer!!,
        productInfo.brandName?.tidy()?.lowercase()?.split(" "),
        productInfo.name!!.tidy().lowercase().split(" "),
        productInfo.variant?.tidy()?.lowercase()?.split(" "),
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
        /**
         * Regex to extract the numbers from a quality field in order to normalise it.
         */
        val quantityRegex = Regex("([\\d.]+)")

        /**
         * A per-retailer list of brands that should be ignored, as these are essentially generic products.
         */
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

    /**
     * Determines whether one product matches another by comparing the arrays, while ignoring the specific order.
     */
    override fun equals(other: Any?): Boolean {
        if (other is MatcherGrouping) {
            if (doesMatch(other) && other.saleType == saleType) {
                if (weight != null && other.weight != null && other.saleType == SaleType.WEIGHT)
                    return true

                if (quantity != null && other.quantity != null) {

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

    /**
     * Whether the two products match, while ignoring the quantity and weight values (so just based on the names alone).
     */
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

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = brand?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (variant?.hashCode() ?: 0)
        result = 31 * result + saleType.hashCode()
        result = 31 * result + (quantity?.hashCode() ?: 0)
        return result
    }
}

/**
 * Tidy up the string and remove specific special characters and double spaces.
 */
fun String.tidy(): String {
    return this.replace(Regex("[()\\-'\"]"), "").replace("\\s+", " ").replace("&", "And").trim()
}