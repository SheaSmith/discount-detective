package com.example.cosc345.shared.models

import kotlin.math.roundToInt

/**
 * Specifies the pricing for a particular product in a particular store, including discounts and multi-buy pricing.
 *
 * @constructor Create a new instance of this object. Some of the nullable parameters are not nullable in practice, but are required to be for Firebase.
 */
data class StorePricingInformation(
    /**
     * The retailer-specified unique ID of a store. For small retailers (for example, those without multiple stores), this will be the same as the retailer ID.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    var store: String? = null,

    /**
     * The price of the product, in cents.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    var price: Int? = null,

    /**
     * The discounted price, even if the promotion is yet to begin. Again this is in cents.
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
     * Whether this price is scraped automatically.
     */
    var automated: Boolean? = null,

    /**
     * Whether this price is 'verified'. Essentially, this is either a price extracted via a verified retailer submitting prices.
     */
    var verified: Boolean? = null
) {
    /**
     * Get the price for this pricing information, e.g. if it is discounted or not, including normalising by weight.
     *
     * @param productInformation The parent product information for this pricing information.
     * @return An int representing the product, for example, $10.50 is 1050.
     */
    fun getPrice(productInformation: RetailerProductInformation): Int {
        var price =
            if (discountPrice == null || price?.let { it < discountPrice!! } == true) {
                price
            } else {
                discountPrice
            }

        if (productInformation.saleType == SaleType.WEIGHT) {
            price = (price!! / (productInformation.weight!!.toDouble() / 1000)).roundToInt()
        }

        return price!!
    }

    /**
     * Get the display price for the best price for this product.
     *
     * @param productInformation The parent product information for this pricing information.
     * @return A pair, with the dollars component (e.g. "$10" for $10.00/kg) as the first value, and
     * the cents component (for example, ".00/kg" for "$10.00/kg) as the second value.
     */
    fun getDisplayPrice(productInformation: RetailerProductInformation): Pair<String, String> =
        getDisplayPrice(productInformation, getPrice(productInformation))

    companion object {
        /**
         * Get the display price for the specified price for this product.
         *
         * @param productInformation The parent product information for this pricing information.
         * @param price The price to get the display price for.
         * @return A pair, with the dollars component (e.g. "$10" for $10.00/kg) as the first value, and
         * the cents component (for example, ".00/kg" for "$10.00/kg) as the second value.
         */
        fun getDisplayPrice(
            productInformation: RetailerProductInformation,
            price: Int
        ): Pair<String, String> {
            val salePrefix = if (productInformation.saleType == SaleType.WEIGHT) {
                "kg"
            } else {
                "ea"
            }

            val priceString = price.toString()

            val dollarComponent = "$${priceString.substring(0, priceString.length - 2)}"
            val centsComponent =
                ".${
                    priceString.substring(
                        priceString.length - 2,
                        priceString.length
                    )
                }/${salePrefix}"

            return Pair(dollarComponent, centsComponent)
        }
    }
}
