package io.github.sheasmith.discountdetective.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.sheasmith.discountdetective.models.ShoppingListItem

/**
 * A dao interface that handles data for the shopping list.
 */
@Dao
interface ShoppingListDao {
    /**
     * Get all products from the shopping_list table.
     * @return A flow containing the IDs which refer to the products, their retailer product information and their pricing.
     */
    @Query("SELECT * FROM shopping_list")
    fun getShoppingList(): LiveData<List<ShoppingListItem>>

    /**
     * Add a new item to the shopping list.
     * @param shoppingListItem item to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingListItem: ShoppingListItem)

    /**
     * Delete an item from the shopping list.
     * @param shoppingListItem to be deleted
     */
    @Delete
    suspend fun delete(shoppingListItem: ShoppingListItem)

    /**
     * Update a shoppingListItem from shoppingList table
     * @param shoppingListItem item to be updated
     */
    @Update
    suspend fun updateChecked(shoppingListItem: ShoppingListItem)
}