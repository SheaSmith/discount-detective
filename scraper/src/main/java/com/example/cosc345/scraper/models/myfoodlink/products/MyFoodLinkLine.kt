package com.example.cosc345.scraper.models.myfoodlink.products

import pl.droidsonroids.jspoon.annotation.Selector

/**
 * An individual product in a MyFoodLink store.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class MyFoodLinkLine(
    /**
     * The name of the product.
     */
    @Selector(".ln__name > span")
    var name: String? = null,

    /**
     * The price of the product.
     */
    @Selector(".item-per-unit-cost", regex = "\\\$([\\d.]+)")
    var price: Double? = null,

    /**
     * The unit price of the product, for example, how much it costs per 100g.
     */
    @Selector(".comparison_price", regex = "\\\$([\\d.]+)")
    var unitPrice: String? = null,

    /**
     * The unit used to calculate the unit price, for example 100g for $5 per 100g.
     */
    @Selector(".comparison_price", regex = "(\\d+[A-z]+)")
    var unitPriceUnit: String? = null,

    /**
     * The amount of savings if there is a discount on, and if that discount amount is greater than or equal to $1.
     */
    @Selector(".saving-amount", regex = "\\\$([\\d.]+)")
    var savingsDollars: String? = null,

    /**
     * The amount of savings if there is a discount on, and if that discount amount is less than $1. This field represents cents as being a whole number, for example $0.70 is represented at 70.
     */
    @Selector(".saving-amount", regex = "(\\d+)c")
    var savingsCents: String? = null,

    /**
     * The quantity used for any multi-buy discounts, for example, for a 2 for $5 discount, this will be 2.
     */
    @Selector("span.label-special:nth-child(2) > span", regex = "(\\d+) for ")
    var multiBuyQuantity: String? = null,

    /**
     * The price for a multi-buy discount, if that discount is greater than or equal to $1, so for 2 for $5, this will be 5.
     */
    @Selector("span.label-special:nth-child(2) > span", regex = "\\d+ for \\\$([\\d.]+)")
    var multiBuyDollars: String? = null,

    /**
     * The price for a multi-buy discount, if that discount is less than $1, for for 2 for 70c, this will be 70.
     */
    @Selector("span.label-special:nth-child(2) > span", regex = "\\d+ for (\\d+)c")
    var multiBuyCents: String? = null,

    /**
     * The image URL for this product.
     */
    @Selector(".ln__img > img", attr = "src")
    var image: String? = null
)
