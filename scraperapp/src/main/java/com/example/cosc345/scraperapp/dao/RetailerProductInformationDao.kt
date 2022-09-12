package com.example.cosc345.scraperapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cosc345.scraperapp.models.StorageRetailerProductInformation
import com.example.cosc345.scraperapp.models.StorageStorePricingInformation

/**
 * The database access object responsible for getting retailer product information.
 */
@Dao
interface RetailerProductInformationDao {
    /**
     * Get all retailer product information, plus the associated store pricing information.
     *
     * @return A map, with [StorageRetailerProductInformation] as the key, and a list of the associated [StorageStorePricingInformation] as the value.
     */
    @Query("SELECT * from RetailerProductInformation JOIN storePricingInformation ON RetailerProductInformation.id = StorePricingInformation.productInfoId AND RetailerProductInformation.retailer = StorePricingInformation.retailerId")
    suspend fun getRetailerProductInfo(): Map<StorageRetailerProductInformation, MutableList<StorageStorePricingInformation>>

    /**
     * Add a series of retailer product information to the database.
     *
     * @param productInfo A list of product information that you want to add/update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setRetailerProductInfo(productInfo: List<StorageRetailerProductInformation>)
}