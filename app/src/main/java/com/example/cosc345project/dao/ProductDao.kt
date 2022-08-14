package com.example.cosc345project.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cosc345project.models.ShoppingListRetailerProductInfo
import kotlinx.coroutines.flow.Flow

/**
 * Get product Ids ordered by retailer
 * Insert product
 * Delete product
 */
@Dao
interface ProductDao {
    @Query("SELECT * FROM shopping_table")
    fun getProductIDs(): Flow<List<ShoppingListRetailerProductInfo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(RetailerProductInfo: ShoppingListRetailerProductInfo)

    @Query("DELETE FROM shopping_table")
    suspend fun deleteAll()
}