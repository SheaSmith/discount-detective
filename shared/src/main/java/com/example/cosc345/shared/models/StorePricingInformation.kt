package com.example.cosc345.shared.models

/**
 * Specifies the pricing for a particular product in a particular store, including discounts and multi-buy pricing.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. Some of the nullable parameters are not nullable in practice, but are required to be for Firebase.
 */
data class StorePricingInformation(
    /**
     * The retailer-specified unique ID of a store. For small retailers (e.g. those without multiple stores), this will be the same as the retailer ID.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    var store: String? = null,

    /**
     * The price of the product, multiplied by 100.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    var price: Int? = null,

    /**
     * The discounted price, even if the promotion is yet to begin. Again this is multiplied by 100.
     */
    var discountPrice: Int? = null,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), then this specifies the quantity for that promotion.
     */
    var multiBuyQuantity: Int? = null,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), this specifies the price for that promotion.
     */
    var multiBuyPrice: Int? = null,

    /**
     * Is the promotion limited to specific club members for that supermarket.
     */
    var clubOnly: Boolean? = null,

    /**
     * Whether this price is 'verified'. Essentially, this is either a price extracted via automated means, or through a verified retailer submitting prices.
     */
    var verified: Boolean? = null
)
