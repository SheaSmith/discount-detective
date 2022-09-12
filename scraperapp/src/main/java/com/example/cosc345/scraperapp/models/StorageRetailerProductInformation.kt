package com.example.cosc345.scraperapp.models

import androidx.room.Entity
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation

/**
 * A version of the normal [RetailerProductInformation] that can be saved in a local Room database.
 */
@Entity(
    tableName = "RetailerProductInformation",
    primaryKeys = ["id", "retailer"]
)
data class StorageRetailerProductInformation(
    /**
     * The retailer-specific ID for this product.
     */
    val id: String,

    /**
     * The unique ID of the retailer, specific to our application.
     */
    val retailer: String,

    /**
     * The product ID associated with this retailer product information (if applicable).
     */
    val productId: String?,

    /**
     * The name of this product (for example, Potato Chips).
     */
    val name: String,

    /**
     * The brand name of the product (for example, Value).
     */
    val brandName: String?,

    /**
     * The variant of the product (for example, Crinkle Cut Chicken).
     */
    val variant: String?,

    /**
     * The method of measurement for the product (for example, by weight). Often products can have multiple ways to measure it (for example, bananas which are either sold individually, or by weight), in this case this is just the primary measurement method.
     */
    val saleType: String,

    /**
     * The quantity of the product, for example 500. This may contain a unit, so may not be appropriate to run calculations on.
     */
    val quantity: String?,

    /**
     * The weight of the product, in grams.
     */
    val weight: Int?,

    /**
     * A list of barcode numbers associated with this product.
     */
    val barcodes: List<String>?,

    /**
     * An URL of the image for this product, hosted on the retailer's server.
     */
    val image: String?,

    /**
     * Whether this product was added automatically.
     */
    val automated: Boolean,

    /**
     * Whether this product was added by the retailer themselves.
     */
    val verified: Boolean
) {
    constructor(productInfo: RetailerProductInformation, productId: String?) : this(
        productInfo.id!!,
        productInfo.retailer!!,
        productId,
        productInfo.name!!,
        productInfo.brandName,
        productInfo.variant,
        productInfo.saleType!!,
        productInfo.quantity,
        productInfo.weight,
        productInfo.barcodes,
        productInfo.image,
        productInfo.automated!!,
        productInfo.verified!!
    )

    /**
     * Convert this back to a normal [RetailerProductInformation] for saving in Firebase, or for processing.
     *
     * @param pricing A list of store pricing information associated with this retailer.
     * @return An instance of the shared [RetailerProductInformation] class, with the same information as this object.
     */
    fun toRetailerProductInformation(pricing: MutableList<StorePricingInformation>): RetailerProductInformation {
        return RetailerProductInformation(
            retailer,
            id,
            name,
            brandName,
            variant,
            saleType,
            quantity,
            weight,
            barcodes,
            image,
            pricing,
            automated,
            verified
        )
    }
}