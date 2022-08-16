package com.example.cosc345project.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345project.models.RetailerProductInfo
import kotlinx.coroutines.flow.Flow

/**
 * Get product Ids ordered by retailer
 * Insert product
 * Delete product
 */
@Dao
interface ProductDao {
    @Query("SELECT * FROM retailerProductInfo")
    fun getProductIDs(): Flow<List<RetailerProductInfo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(RetailerProductInfo: RetailerProductInfo)

    @Query("DELETE FROM retailerProductInfo")
    suspend fun deleteAll()
}