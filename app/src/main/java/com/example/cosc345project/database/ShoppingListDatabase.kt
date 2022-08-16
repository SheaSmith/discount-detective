package com.example.cosc345project.database
import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cosc345project.dao.ProductDao
import com.example.cosc345project.models.RetailerProductInfo
import kotlinx.coroutines.internal.synchronized


/**
 * Room database
 * - Top layer of SQLite database
 */
@Database(entities = [RetailerProductInfo::class], version=1)
abstract class ShoppingListDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao




}