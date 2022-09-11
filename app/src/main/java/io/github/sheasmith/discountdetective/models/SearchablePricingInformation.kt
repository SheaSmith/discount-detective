package io.github.sheasmith.discountdetective.models

import androidx.appsearch.annotation.Document
import com.example.cosc345.shared.models.StorePricingInformation

/**
 * A version of the [StorePricingInformation] specifically for
 * inserting into the AppSearch database.
 */
@Document
data class SearchablePricingInformation(
    /**
     * The namespace for this document. We always set this to "all", as a namespace is requried, but
     * we don't need to filter based on namespace.
     */
    @Document.Namespace
    val namespace: String = "all",

    /**
     * The retailer for this pricing information.
     */
    @Document.StringProperty
    val retailer: String,

    /**
     * The retailer-specified unique ID of a store. For small retailers (for example, those without multiple stores), this will be the same as the retailer ID.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     *
     * This does not need to be globally unique, as AppSearch keeps IDs separate per nested document.
     */
    @Document.Id
    val store: String,

    /**
     * The price of the product, multiplied by 100.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    @Document.LongProperty
    val price: Int? = null,

    /**
     * The discounted price, even if the promotion is yet to begin. Again this is multiplied by 100.
     */
    @Document.LongProperty
    val discountPrice: Int? = null,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), then this specifies the quantity for that promotion.
     */
    @Document.LongProperty
    val multiBuyQuantity: Int? = null,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), this specifies the price for that promotion.
     */
    @Document.LongProperty
    val multiBuyPrice: Int? = null,

    /**
     * Is the promotion limited to specific club members for that supermarket.
     */
    @Document.BooleanProperty
    val clubOnly: Boolean? = null,

    /**
     * Whether this price is scraped automatically.
     */
    @Document.BooleanProperty
    val automated: Boolean,

    /**
     * Whether this price is 'verified'. Essentially, this is either a price extracted via a verified retailer submitting prices.
     */
    @Document.BooleanProperty
    val verified: Boolean
) {
    /**
     * Convert this object back to a standard [StorePricingInformation].
     */
    fun toStorePricingInformation(): StorePricingInformation = StorePricingInformation(
        store,
        price,
        discountPrice,
        multiBuyQuantity,
        multiBuyPrice,
        clubOnly,
        automated,
        verified
    )

    /**
     * Create this searchable version based off the standard [StorePricingInformation].
     *
     * @param pricingInformation The pricing information to use to initialise this.
     * @param retailerId The retailer ID to use.
     */
    constructor(pricingInformation: StorePricingInformation, retailerId: String) : this(
        "all",
        retailerId,
        pricingInformation.store!!,
        pricingInformation.price,
        pricingInformation.discountPrice,
        pricingInformation.multiBuyQuantity,
        pricingInformation.multiBuyPrice,
        pricingInformation.clubOnly,
        pricingInformation.automated ?: true,
        pricingInformation.verified ?: false
    )
}
