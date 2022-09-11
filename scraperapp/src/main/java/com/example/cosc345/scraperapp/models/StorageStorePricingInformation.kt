package com.example.cosc345.scraperapp.models

import androidx.room.Entity
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation

/**
 * A version of the normal [StorePricingInformation] that can be saved in a local Room database.
 */
@Entity(
    tableName = "StorePricingInformation",
    primaryKeys = ["productInfoId", "retailerId", "storeId"]
)
data class StorageStorePricingInformation(
    /**
     * The retailer product information ID that this pricing information is associated with.
     */
    val productInfoId: String,

    /**
     * The ID of the retailer this pricing information is associated with.
     */
    val retailerId: String,

    /**
     * The retailer-specified unique ID of a store. For small retailers (for example, those without multiple stores), this will be the same as the retailer ID.
     */
    val storeId: String,

    /**
     * The price of the product, in cents.
     */
    val price: Int?,

    /**
     * The discounted price, even if the promotion is yet to begin. Again this is in cents.
     */
    val discountPrice: Int?,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), then this specifies the quantity for that promotion (for example, 2).
     */
    val multiBuyQuantity: Int?,

    /**
     * If there is a multi-buy promotion (for example, buy 2 for $5), this specifies the price for that promotion (for example, $5) in cents.
     */
    val multiBuyPrice: Int?,

    /**
     * Is the promotion limited to specific club members for that supermarket.
     */
    val clubOnly: Boolean?,

    /**
     * Whether this price is scraped automatically.
     */
    val automated: Boolean?,

    /**
     * Whether this price is 'verified'. Essentially, this is either a price extracted via a verified retailer submitting prices.
     */
    val verified: Boolean?
) {
    constructor(
        retailerProductInfo: RetailerProductInformation,
        storePricingInformation: StorePricingInformation
    ) : this(
        retailerProductInfo.id!!,
        retailerProductInfo.retailer!!,
        storePricingInformation.store!!,
        storePricingInformation.price,
        storePricingInformation.discountPrice,
        storePricingInformation.multiBuyQuantity,
        storePricingInformation.multiBuyPrice,
        storePricingInformation.clubOnly,
        storePricingInformation.automated,
        storePricingInformation.verified
    )

    /**
     * Convert this back to a normal [StorePricingInformation] for saving in Firebase, or for processing.
     *
     * @return An instance of the shared [StorePricingInformation] class, with the same information as this object.
     */
    fun toStorePricingInformation(): StorePricingInformation =
        StorePricingInformation(
            storeId,
            price,
            discountPrice,
            multiBuyQuantity,
            multiBuyPrice,
            clubOnly,
            automated,
            verified
        )
}
