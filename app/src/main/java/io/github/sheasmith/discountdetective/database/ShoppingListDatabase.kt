package io.github.sheasmith.discountdetective.database
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.sheasmith.discountdetective.dao.ProductDao
import io.github.sheasmith.discountdetective.models.RetailerProductInfo


/**
 * Room database
 * - Top layer of SQLite database
 */
@Database(entities = [RetailerProductInfo::class], version = 2)
abstract class ShoppingListDatabase : RoomDatabase() {

    /**
     * Get the dao for manipulating the shopping list.
     */
    abstract fun productDao(): ProductDao
}