package com.example.cosc345project.models

import androidx.appsearch.annotation.Document
import androidx.appsearch.app.AppSearchSchema
import com.example.cosc345.shared.models.RetailerProductInformation

@Document
data class SearchableRetailerProductInformation(
    @Document.Namespace
    val namespace: String = "all",

    @Document.StringProperty
    val retailer: String,

    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val brandName: String?,

    @Document.StringProperty(
        indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES,
        required = true
    )
    val name: String,

    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val variant: String?,

    @Document.Id
    val id: String,

    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_EXACT_TERMS)
    val barcodes: String?,

    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val quantity: String?,

    @Document.LongProperty
    val weight: Int?,

    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE)
    val saleType: String,

    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE)
    val image: String?,

    @Document.DocumentProperty(indexNestedProperties = false)
    val pricing: List<SearchablePricingInformation>,

    @Document.BooleanProperty
    val automated: Boolean,

    @Document.BooleanProperty
    val verified: Boolean,

    @Document.BooleanProperty
    val local: Boolean
) {
    fun toRetailerProductInformation(): RetailerProductInformation =
        RetailerProductInformation(
            retailer,
            id,
            name,
            brandName,
            variant,
            saleType,
            quantity,
            weight,
            barcodes?.split(" "),
            image,
            pricing.map { it.toStorePricingInformation() }.toMutableList(),
            automated,
            verified
        )

    constructor(info: RetailerProductInformation, local: Boolean) : this(
        "all",
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
        info.pricing!!.map { SearchablePricingInformation(it, info.retailer!!) },
        info.automated ?: true,
        info.verified ?: false,
        local
    )
}