package io.github.sheasmith.discountdetective.models

import androidx.appsearch.annotation.Document
import com.example.cosc345.shared.models.Product

/**
 * A [Product] that is able to be inserted into the AppSearch database.
 */
@Document
data class SearchableProduct(
    /**
     * The namespace for this document. We always set this to "all", as a namespace is requried, but
     * we don't need to filter based on namespace.
     */
    @Document.Namespace
    val namespace: String = "all",

    /**
     * The ID of this product.
     */
    @Document.Id
    val id: String,

    /**
     * The number of information sets associated with this product. We use this as a score to
     * prioritise more "popular" items.
     */
    @Document.Score
    val score: Int,

    /**
     * A list of information associated with this product. We index on these properties as well.
     */
    @Document.DocumentProperty(indexNestedProperties = true)
    val information: List<SearchableRetailerProductInformation>
) {
    /**
     * Create this searchable product from the [Product] model.
     *
     * @param product The product to create this searchable version from.
     * @param id The ID of the product.
     * @param localMap A map, containing the ID of the retailer as a key and whether the store is
     * local or not as a value.
     */
    constructor(product: Product, id: String, localMap: Map<String, Boolean>) : this(
        id = id,
        score = calculateScore(product),
        information = product.information!!.map {
            SearchableRetailerProductInformation(
                it,
                localMap[it.retailer!!] ?: false
            )
        })

    /**
     * Convert the searchable product back to the standard [Product].
     *
     * @return A pair with the ID of the product as the ID, and the product as the value.
     */
    fun toProduct(): Pair<String, Product> {
        return Pair(
            id,
            Product(information.map { it.toRetailerProductInformation() }.toMutableList())
        )
    }

    companion object {
        private fun calculateScore(product: Product): Int {
            val bestInformation = product.getBestInformation()

            val wordScore = 100 - ((bestInformation.name?.count { it == ' ' } ?: 0) +
                    (bestInformation.brandName?.count { it == ' ' } ?: 0) +
                    (bestInformation.variant?.count { it == ' ' } ?: 0))

            val retailersScore = product.information!!.size * 3

            return wordScore + retailersScore
        }
    }
}