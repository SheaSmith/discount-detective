package com.example.cosc345project.models

import androidx.appsearch.annotation.Document
import com.example.cosc345.shared.models.Product

@Document
data class SearchableProduct(
    @Document.Namespace
    val namespace: String = "all",

    @Document.Id
    val id: String,

    @Document.Score
    val size: Int,

    @Document.DocumentProperty(indexNestedProperties = true)
    val information: List<SearchableRetailerProductInformation>?
) {
    constructor(product: Product, id: String, localMap: Map<String, Boolean>) : this(
        id = id,
        size = product.information!!.size,
        information = product.information!!.map {
            SearchableRetailerProductInformation(
                it,
                product.information!!.size,
                localMap[it.retailer!!] ?: false
            )
        })
}