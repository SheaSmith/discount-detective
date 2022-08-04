package com.example.cosc345.scraperapp.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.cosc345.scraperapp.models.StorageStorePricingInformation

@Dao
interface StorePricingInformationDao {
    @Insert
    suspend fun insertPricingInformation(priceInformation: List<StorageStorePricingInformation>)
}