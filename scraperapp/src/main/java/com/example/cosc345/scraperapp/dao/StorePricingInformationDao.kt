package com.example.cosc345.scraperapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.cosc345.scraperapp.models.StorageStorePricingInformation

/**
 * The database access object responsible for managing store pricing information.
 */
@Dao
interface StorePricingInformationDao {
    /**
     * Insert a series of store pricing information into the database.
     *
     * @param priceInformation The list of pricing information you want to add/update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPricingInformation(priceInformation: List<StorageStorePricingInformation>)
}