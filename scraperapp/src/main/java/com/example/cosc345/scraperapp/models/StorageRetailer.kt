package com.example.cosc345.scraperapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

@Entity(tableName = "Retailers")
data class StorageRetailer(
    @PrimaryKey
    val id: String,
    val name: String,
    val automated: Boolean
) {
    constructor(retailer: Retailer, id: String) : this(
        id,
        retailer.name!!,
        retailer.automated!!
    )

    fun toRetailer(stores: List<Store>): Retailer {
        return Retailer(
            name,
            automated,
            stores
        )
    }
}
