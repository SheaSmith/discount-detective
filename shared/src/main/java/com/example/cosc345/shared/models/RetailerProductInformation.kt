package com.example.cosc345.shared.models

/**
 * The information about a product, specific to a particular retailer.
 */
data class RetailerProductInformation(
    /**
     * The unique ID of the retailer, specific to our application.
     *
     * Not nullable in practice, but Firebase requires an object with no arguments for the database.
     */
    val retailer: String? = null,

    /**
     * The retailer-specific ID for this product.
     *
     * Required in practice, however Firebase requires nullable values.
     */
    val id: String? = null,

    /**
     * The name of this product (for example, Potato Chips).
     *
     * Required in practice, however Firebase requires nullable values.
     */
    var name: String? = null,

    /**
     * The brand name of the product (for example, Value).
     */
    var brandName: String? = null,

    /**
     * The variant of the product (for example, Crinkle Cut Chicken).
     */
    var variant: String? = null,

    /**
     * The method of measurement for the product (for example, by weight). Often products can have multiple ways to measure it (for example, bananas which are either sold individually, or by weight), in this case this is just the primary measurement method.
     *
     * Required in practice, however Firebase requires nullable values.
     */
    var saleType: SaleType? = null,

    /**
     * The unit of measurement for the product, for example kg or 50pk.
     */
    var unit: String? = null,

    /**
     * The quantity of the product, for example 500. This may contain a unit, so may not be appropriate to run calculations on.
     */
    var quantity: String? = null,

    /**
     * The weight of the product, in grams.
     */
    var weight: Int? = null,

    /**
     * A list of barcode numbers associated with this product.
     */
    var barcodes: List<String>? = null,

    /**
     * An URL of the image for this product, hosted on the retailer's server.
     *
     * Required in practice, however Firebase requires nullable values.
     */
    var image: String? = null,

    /**
     * The pricing for the different stores for this retailer.
     */
    var pricing: List<StorePricingInformation>? = null
)