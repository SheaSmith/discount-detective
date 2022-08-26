package com.example.cosc345.scraperapp.models

import androidx.room.Entity
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation

/**
 * Instances of this StorageRetailerProductInformation class store the information of each product
 * for use throughout the app.
 *
 */
@Entity(
    tableName = "RetailerProductInformation",
    primaryKeys = ["id", "retailer"]
)
data class StorageRetailerProductInformation(
    val id: String,
    val retailer: String,
    val productId: String?,
    val name: String,
    val brandName: String?,
    val variant: String?,
    val saleType: String,
    val quantity: String?,
    val weight: Int?,
    val barcodes: List<String>?,
    val image: String?,
    val automated: Boolean,
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