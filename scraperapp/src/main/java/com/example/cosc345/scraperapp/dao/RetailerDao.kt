package com.example.cosc345.scraperapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cosc345.scraperapp.models.StorageRetailer
import com.example.cosc345.scraperapp.models.StorageStore

/**
 * The database access object responsible for saving retailer information.
 */
@Dao
interface RetailerDao {
    /**
     * Get all retailers and stores from the database.
     *
     * @return A map, with the [StorageRetailer] as the key, and the list of [StorageStore] associated with this retailer.
     */
    @Query("SELECT * FROM Retailers JOIN Stores ON Retailers.id = Stores.storeRetailer")
    suspend fun getRetailers(): Map<StorageRetailer, List<StorageStore>>

    /**
     * Add a series of new retailers to the database.
     *
     * @param retailers The retailers to add to the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailers(retailers: List<StorageRetailer>)
}