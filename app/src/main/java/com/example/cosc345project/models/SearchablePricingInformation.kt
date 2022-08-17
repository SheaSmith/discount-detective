package com.example.cosc345project.models

import androidx.appsearch.annotation.Document
import com.example.cosc345.shared.models.StorePricingInformation

@Document
data class SearchablePricingInformation(
    @Document.Namespace
    val namespace: String = "all",

    @Document.StringProperty
    val retailer: String,

    /**
     * The retailer-specified unique ID of a store. For small retailers (for example, those without multiple stores), this will be the same as the retailer ID.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
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
    fun toStorePricingInformation() = StorePricingInformation(
        store,
        price,
        discountPrice,
        multiBuyQuantity,
        multiBuyPrice,
        clubOnly,
        automated,
        verified
    )

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
