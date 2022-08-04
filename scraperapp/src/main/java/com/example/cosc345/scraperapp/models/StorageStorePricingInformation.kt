package com.example.cosc345.scraperapp.models

import androidx.room.Entity
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation

@Entity(
    tableName = "StorePricingInformation",
    primaryKeys = ["productInfoId", "retailerId", "storeId"]
)
data class StorageStorePricingInformation(
    val productInfoId: String,
    val retailerId: String,
    val storeId: String,
    val price: Int?,
    val discountPrice: Int?,
    val multiBuyQuantity: Int?,
    val multiBuyPrice: Int?,
    val clubOnly: Boolean?,
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
        storePricingInformation.verified
    )

    fun toStorePricingInformation(): StorePricingInformation =
        StorePricingInformation(
            storeId,
            price,
            discountPrice,
            multiBuyQuantity,
            multiBuyPrice,
            clubOnly,
            verified
        )
}
