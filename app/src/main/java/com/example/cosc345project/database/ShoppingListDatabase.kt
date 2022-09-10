package com.example.cosc345project.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cosc345project.dao.ShoppingListDao
import com.example.cosc345project.models.ShoppingListItem

/**
 * The database responsible for storing shopping list items.
 */
@Database(entities = [ShoppingListItem::class], version = 3)
abstract class ShoppingListDatabase : RoomDatabase() {

    /**
     * Get the dao for manipulating the shopping list.
     */
    abstract fun productDao(): ShoppingListDao
}