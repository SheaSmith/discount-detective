package com.example.cosc345project.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for the shopping list App
 *
 * Just store product IDs
 * @Entity class represent a SQLite table
 */
@Entity(tableName = "shopping_table")
class ShoppingListRetailerProductInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "productID") val productID: String) {


}