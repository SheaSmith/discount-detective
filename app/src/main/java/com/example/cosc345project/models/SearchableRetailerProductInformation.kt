package com.example.cosc345project.models

import android.app.appsearch.AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_EXACT_TERMS
import android.app.appsearch.AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES
import androidx.appsearch.annotation.Document
import androidx.appsearch.app.AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE
import com.example.cosc345.shared.models.RetailerProductInformation

@Document
data class SearchableRetailerProductInformation(
    @Document.Namespace
    val retailer: String,

    @Document.StringProperty(indexingType = INDEXING_TYPE_PREFIXES)
    val brandName: String?,

    @Document.StringProperty(indexingType = INDEXING_TYPE_PREFIXES, required = true)
    val name: String,

    @Document.StringProperty(indexingType = INDEXING_TYPE_PREFIXES)
    val variant: String?,

    @Document.Id
    val id: String,

    @Document.StringProperty(indexingType = INDEXING_TYPE_EXACT_TERMS)
    val barcodes: String?,

    @Document.StringProperty(indexingType = INDEXING_TYPE_PREFIXES)
    val quantity: String?,

    @Document.LongProperty
    val weight: Int?,

    @Document.StringProperty(indexingType = INDEXING_TYPE_NONE)
    val saleType: String,

    @Document.StringProperty(indexingType = INDEXING_TYPE_NONE)
    val image: String?,

    @Document.DocumentProperty(indexNestedProperties = false)
    val pricing: List<SearchablePricingInformation>
) {
    constructor(info: RetailerProductInformation) : this(
        info.retailer!!,
        info.brandName,
        info.name!!,
        info.variant,
        info.id!!,
        info.barcodes?.joinToString { " " },
        info.quantity,
        info.weight,
        info.saleType!!,
        info.image,
        info.pricing!!.map { SearchablePricingInformation(it, info.retailer!!) }
    )
}