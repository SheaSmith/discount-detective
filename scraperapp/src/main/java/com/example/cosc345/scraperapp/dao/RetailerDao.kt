package com.example.cosc345.scraperapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cosc345.scraperapp.models.StorageRetailer
import com.example.cosc345.scraperapp.models.StorageStore

@Dao
interface RetailerDao {
    @Query("SELECT * FROM Retailers JOIN Stores ON Retailers.id = Stores.retailer")
    suspend fun getRetailers(): Map<StorageRetailer, List<StorageStore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailers(retailers: List<StorageRetailer>)
}