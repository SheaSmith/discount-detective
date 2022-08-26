package com.example.cosc345project.dao

import androidx.room.*
import com.example.cosc345project.models.RetailerProductInfo
import kotlinx.coroutines.flow.Flow

/**
 * A dao that handles data manipulation for the shopping list.
 */
@Dao
interface ProductDao {
    @Query("SELECT * FROM retailerProductInfo")
    fun getProductIDs(): Flow<List<RetailerProductInfo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(RetailerProductInfo: RetailerProductInfo)

    @Delete
    suspend fun delete(RetailerProductInfo: RetailerProductInfo)

    @Query("DELETE FROM retailerProductInfo")
    suspend fun deleteAll()
}