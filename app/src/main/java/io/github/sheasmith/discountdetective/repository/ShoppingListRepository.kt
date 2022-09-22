package io.github.sheasmith.discountdetective.repository

import io.github.sheasmith.discountdetective.dao.ShoppingListDao
import io.github.sheasmith.discountdetective.models.ShoppingListItem
import kotlinx.coroutines.flow.Flow
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
    var shoppingList: Flow<List<ShoppingListItem>> = shoppingListDao.getShoppingList()

    /**
     * Insert a product into the shopping list
     *
     * @param shoppingListShoppingListItem The shopping list item to add to the database.
     */
    suspend fun addToShoppingList(shoppingListShoppingListItem: ShoppingListItem) {
        shoppingListDao.insert(shoppingListShoppingListItem)
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
     * @param shoppingListShoppingListItem the item to update.
     */
    suspend fun updateChecked(shoppingListShoppingListItem: ShoppingListItem) {
        shoppingListDao.updateChecked(shoppingListShoppingListItem)
    }

}