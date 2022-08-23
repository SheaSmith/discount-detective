package com.example.cosc345project.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for the shopping list App
 **
 * @Entity class represent a SQLite table
 */
@Entity(tableName = "retailerProductInfo")
class RetailerProductInfo(
    @PrimaryKey
    val productID: String,
    @ColumnInfo(name = "retailerProductInformation")
    val retailerProductInformationID: String,
    @ColumnInfo(name = "storePricingID")
    val storePricingInformationID: String,
    @ColumnInfo(name = "quantity")
    val quantity: Int
)