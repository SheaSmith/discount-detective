package com.example.cosc345project.models

import androidx.appsearch.annotation.Document
import com.example.cosc345.shared.models.StorePricingInformation

@Document
data class SearchablePricingInformation(
    @Document.Namespace
    var retailer: String,

    /**
     * The retailer-specified unique ID of a store. For small retailers (for example, those without multiple stores), this will be the same as the retailer ID.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    @Document.Id
    var store: String,

    /**
     * The price of the product, multiplied by 100.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    @Document.LongProperty
    var price: Int? = null,

    /**
     * The discounted price, even if the promotion is yet to begin. Again this is multiplied by 100.
     */
    @Document.LongProperty
    var discountPrice: Int? = null,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), then this specifies the quantity for that promotion.
     */
    @Document.LongProperty
    var multiBuyQuantity: Int? = null,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), this specifies the price for that promotion.
     */
    @Document.LongProperty
    var multiBuyPrice: Int? = null,

    /**
     * Is the promotion limited to specific club members for that supermarket.
     */
    @Document.BooleanProperty
    var clubOnly: Boolean? = null,

    /**
     * Whether this price is 'verified'. Essentially, this is either a price extracted via automated means, or through a verified retailer submitting prices.
     */
    @Document.BooleanProperty
    var verified: Boolean?
) {
    constructor(pricingInformation: StorePricingInformation, retailerId: String) : this(
        retailerId,
        pricingInformation.store!!,
        pricingInformation.price,
        pricingInformation.discountPrice,
        pricingInformation.multiBuyQuantity,
        pricingInformation.multiBuyPrice,
        pricingInformation.clubOnly,
        pricingInformation.verified
    )
}
