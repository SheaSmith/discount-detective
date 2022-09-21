package io.github.sheasmith.discountdetective.models

import androidx.room.Entity

/**
 * Entity for the shopping list App
 **
 * @Entity class represent a SQLite table
 */
@Entity(
    primaryKeys = ["productId", "retailerProductInformationId", "storeId"],
    tableName = "shopping_list",
)
data class ShoppingListItem(
    /**
     * The ID of the product in the shopping list.
     */
    val productId: String,

    /**
     * The ID of the retailer product information in the shopping list.
     */
    val retailerProductInformationId: String,

    /**
     * The ID of the store whose pricing should be used for the shopping list.
     */
    val storeId: String,

    /**
     * The quantity added to the shopping list.
     */
    val quantity: Int,

    /**
     * If the item has been checked in the shopping list
     */
    var checked: Boolean = false


)