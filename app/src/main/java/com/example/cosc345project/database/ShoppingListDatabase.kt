package com.example.cosc345project.database
import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cosc345project.dao.ProductDao
import com.example.cosc345project.models.ShoppingListRetailerProductInfo
import kotlinx.coroutines.internal.synchronized


/**
 * Room database
 * - Top layer of SQLite database
 */
//Annotate class to be Room database with table (entity) of shopping list class
@Database(entities = arrayOf(ShoppingListRetailerProductInfo::class), version=1, exportSchema=false)
public abstract class ShoppingListDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao



}