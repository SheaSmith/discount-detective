package com.example.cosc345.shared.models

import java.time.ZonedDateTime

/**
 * Specifies the pricing for a particular product in a particular store, including discounts and multi-buy pricing.
 */
data class StorePricingInformation(
    /**
     * The retailer-specified unique ID of a store. For small retailers (e.g. those without multiple stores), this will be the same as the retailer ID.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    val store: String? = null,

    /**
     * The price of the product, multiplied by 100.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    val price: Int? = null,

    /**
     * The discounted price, even if the promotion is yet to begin. Again this is multiplied by 100.
     */
    val discountPrice: Int? = null,

    /**
     * The date and time when the discount starts (if applicable). Not all stores use this, so if it missing, we assume the discount is current.
     */
    val discountStart: ZonedDateTime? = null,

    /**
     * The date and time when the discount ends (if applicable). Again, not all stores use this, so if it missing, we assume the discount is current.
     */
    val discountEnd: ZonedDateTime? = null,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), then this specifies the quantity for that promotion.
     */
    val multiBuyQuantity: Int? = null,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), this specifies the price for that promotion.
     */
    val multiBuyPrice: Int? = null,

    /**
     * Is the promotion limited to specific club members for that supermarket.
     */
    val clubOnly: Boolean? = null,

    /**
     * Whether this price is 'verified'. Essentially, this is either a price extracted via automated means, or through a verified retailer submitting prices.
     */
    val verified: Boolean? = null
)
