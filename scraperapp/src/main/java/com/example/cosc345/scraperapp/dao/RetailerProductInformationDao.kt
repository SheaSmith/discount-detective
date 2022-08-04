package com.example.cosc345.scraperapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cosc345.scraperapp.models.StorageRetailerProductInformation
import com.example.cosc345.scraperapp.models.StorageStorePricingInformation

@Dao
interface RetailerProductInformationDao {
    @Query("SELECT * from RetailerProductInformation JOIN storePricingInformation ON RetailerProductInformation.id = StorePricingInformation.productInfoId AND RetailerProductInformation.retailer = StorePricingInformation.retailerId")
    suspend fun getRetailerProductInfo(): Map<StorageRetailerProductInformation, MutableList<StorageStorePricingInformation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setRetailerProductInfo(productInfo: List<StorageRetailerProductInformation>)
}