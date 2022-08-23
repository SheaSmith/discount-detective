package com.example.cosc345project.models

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Entity for the shopping list App
 **
 * @Entity class represent a SQLite table
 */
@Entity(
    primaryKeys = ["productID", "retailerProductInformation", "storePricingID"],
    tableName = "retailerProductInfo",
)
class RetailerProductInfo(
    val productID: String,
    @ColumnInfo(name = "retailerProductInformation")
    val retailerProductInformationID: String,
    @ColumnInfo(name = "storePricingID")
    val storePricingInformationID: String,
    @ColumnInfo(name = "quantity")
    val quantity: Int
)