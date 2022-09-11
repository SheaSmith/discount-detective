package com.example.cosc345.scraperapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.cosc345.scraperapp.models.StorageStore

/**
 * The database access object responsible for managing stores.
 */
@Dao
interface StoreDao {
    /**
     * Add a series of stores to the database.
     *
     * @param stores The list of stores you want to add/update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(stores: List<StorageStore>)
}