package com.example.cosc345project.models

import androidx.room.*
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation
import org.json.JSONObject
import javax.xml.transform.Source

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
    @ColumnInfo(name = "storePricingInformation")
    val storePricingInformationID: String,
    @ColumnInfo(name = "quantity")
    val quantity: Int
) {

}