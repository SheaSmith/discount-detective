package com.example.cosc345project.models

import androidx.appsearch.annotation.Document
import androidx.appsearch.app.AppSearchSchema
import com.example.cosc345.shared.models.RetailerProductInformation

/**
 * A version of [com.example.cosc345.shared.models.RetailerProductInformation] that can be inserted into the AppSearch database.
 */
@Document
data class SearchableRetailerProductInformation(
    /**
     * The namespace for this document. We always set this to "all", as a namespace is requried, but
     * we don't need to filter based on namespace.
     */
    @Document.Namespace
    val namespace: String = "all",

    /**
     * The unique ID of the retailer, specific to our application.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    @Document.StringProperty
    val retailer: String,

    /**
     * The brand name of the product (for example, Value).
     */
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val brandName: String?,

    /**
     * The name of this product (for example, Potato Chips).
     *
     * Required in practice, however Firebase requires nullable values.
     */
    @Document.StringProperty(
        indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES,
        required = true
    )
    val name: String,

    /**
     * The variant of the product (for example, Crinkle Cut Chicken).
     */
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val variant: String?,

    /**
     * The retailer-specific ID for this product.
     *
     * Required in practice, however Firebase requires nullable values.
     */
    @Document.Id
    val id: String,

    /**
     * A list of barcode numbers associated with this product, joined by spaces into a single string.
     */
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val barcodes: String?,

    /**
     * The quantity of the product, for example 500. This may contain a unit, so may not be appropriate to run calculations on.
     */
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val quantity: String?,

    /**
     * The weight of the product, in grams.
     */
    @Document.LongProperty
    val weight: Int?,

    /**
     * The method of measurement for the product (for example, by weight). Often products can have multiple ways to measure it (for example, bananas which are either sold individually, or by weight), in this case this is just the primary measurement method.
     *
     * Required in practice, however Firebase requires nullable values.
     */
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE)
    val saleType: String,

    /**
     * An URL of the image for this product, hosted on the retailer's server.
     */
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_EXACT_TERMS)
    val image: String?,

    /**
     * The pricing for the different stores for this retailer. We don't need to index on these properties.
     */
    @Document.DocumentProperty(indexNestedProperties = false)
    val pricing: List<SearchablePricingInformation>,

    /**
     * Whether this product was added automatically.
     */
    @Document.BooleanProperty
    val automated: Boolean,

    /**
     * Whether this product was added by the retailer themselves.
     */
    @Document.BooleanProperty
    val verified: Boolean,

    /**
     * Whether this product is considered local or not.
     */
    @Document.BooleanProperty
    val local: Boolean
) {
    /**
     * Convert back to a [com.example.cosc345.shared.models.RetailerProductInformation].
     *
     * @return A retailer product information based on this model.
     */
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

    /**
     * Create a new instance of this searchable model, based off of [com.example.cosc345.shared.models.RetailerProductInformation].
     *
     * @param info The [com.example.cosc345.shared.models.RetailerProductInformation] to use to
     * construct this model.
     * @param local Whether the retailer for this model is local or not.
     */
    constructor(info: RetailerProductInformation, local: Boolean) : this(
        "all",
        info.retailer!!,
        info.brandName,
        info.name!!,
        info.variant,
        info.id!!,
        info.barcodes?.joinToString(" "),
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