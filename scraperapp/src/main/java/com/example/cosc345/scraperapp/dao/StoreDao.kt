package com.example.cosc345.scraperapp.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.cosc345.scraperapp.models.StorageStore

@Dao
interface StoreDao {
    @Insert
    suspend fun insertStores(stores: List<StorageStore>)
}