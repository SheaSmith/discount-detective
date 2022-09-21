package io.github.sheasmith.discountdetective.database
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.sheasmith.discountdetective.dao.ShoppingListDao
import io.github.sheasmith.discountdetective.models.ShoppingListItem

/**
 * The database responsible for storing shopping list items.
 */
@Database(entities = [ShoppingListItem::class], version = 5)
abstract class ShoppingListDatabase : RoomDatabase() {

    /**
     * Get the dao for manipulating the shopping list.
     */
    abstract fun productDao(): ShoppingListDao
}