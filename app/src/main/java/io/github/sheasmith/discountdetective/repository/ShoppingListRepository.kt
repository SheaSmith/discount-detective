package io.github.sheasmith.discountdetective.repository

import androidx.lifecycle.LiveData
import io.github.sheasmith.discountdetective.dao.ShoppingListDao
import io.github.sheasmith.discountdetective.models.ShoppingListItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The repository responsible for shopping list related tasks.
 *
 * Basically, this holds the lists of products that belong to different criteria. Then the
 * viewmodel's mutableStates are updated.
 *
 * Essentially the mutable states are like listeners but without all the extra code.
 *
 * So when someone 'adds' a product, is stored here as list.
 */
@Singleton
class ShoppingListRepository @Inject constructor(
    private val shoppingListDao: ShoppingListDao
) {
    /**
     * A flow containing all of the product IDs, updated whenever a new item is added.
     */
    var shoppingList: LiveData<List<ShoppingListItem>> = shoppingListDao.getShoppingList()

    /**
     * Insert a product into the shopping list
     *
     * @param shoppingListItem The shopping list item to add to the database.
     */
    suspend fun addToShoppingList(shoppingListItem: ShoppingListItem) {
        val existingItem = shoppingListDao.getShoppingListItem(
            shoppingListItem.productId,
            shoppingListItem.retailerProductInformationId,
            shoppingListItem.storeId
        )
        if (existingItem == null) {
            shoppingListDao.insert(shoppingListItem)
        } else {
            existingItem.quantity += shoppingListItem.quantity
            shoppingListDao.update(existingItem)
        }
    }

    /**
     * Delete a product from the shopping list.
     *
     * @param shoppingListShoppingListItem The product to delete.
     */
    suspend fun deleteFromShoppingList(shoppingListShoppingListItem: ShoppingListItem) {
        shoppingListDao.delete(shoppingListShoppingListItem)
    }

    /**
     * Update a shopping list item.
     *
     * @param shoppingListShoppingListItem the item to update.
     */
    suspend fun update(shoppingListShoppingListItem: ShoppingListItem) {
        shoppingListDao.update(shoppingListShoppingListItem)
    }

}