package com.example.cosc345project.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cosc345project.dao.ProductDao
import com.example.cosc345project.models.RetailerProductInfo


/**
 * Room database
 * - Top layer of SQLite database
 */
@Database(entities = [RetailerProductInfo::class], version = 2)
abstract class ShoppingListDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao




}