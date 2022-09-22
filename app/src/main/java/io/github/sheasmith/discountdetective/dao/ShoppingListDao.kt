package io.github.sheasmith.discountdetective.dao

import androidx.room.*
import io.github.sheasmith.discountdetective.models.ShoppingListItem
import kotlinx.coroutines.flow.Flow

/**
 * A dao that handles data manipulation for the shopping list.
 */
@Dao
interface ShoppingListDao {
    /**
     * Get all products in the shopping list.
     *
     * @return A flow containing the IDs which refer to the products, their retailer product information and their pricing.
     */
    @Query("SELECT * FROM shopping_list")
    fun getShoppingList(): Flow<List<ShoppingListItem>>

    /**
     * Add a new item to the shopping list.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingListItem: ShoppingListItem)

    /**
     * Delete an item from the shopping list.
     */
    @Delete
    suspend fun delete(shoppingListItem: ShoppingListItem)

    /**
     * Update the checked state of an item
     */
//    @Query(
//        "UPDATE shopping_list SET " +
//                "checked = :checked WHERE " +
//                "productId = :productId AND " +
//                "retailerProductInformationId = :retailerProductInformationId AND " +
//                "storeId = :storeId"
//    )
    @Update
    suspend fun updateChecked(shoppingListItem: ShoppingListItem)
}