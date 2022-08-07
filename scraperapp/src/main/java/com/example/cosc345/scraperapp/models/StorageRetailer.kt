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
    val automated: Boolean,
    val verified: Boolean,
    val colourLight: Long,
    val onColourLight: Long,
    val onColourDark: Long,
    val colourDark: Long,
    val initialism: String,
    val local: Boolean
) {
    constructor(retailer: Retailer, id: String) : this(
        id,
        retailer.name!!,
        retailer.automated!!,
        retailer.verified!!,
        retailer.colourLight!!,
        retailer.onColourLight!!,
        retailer.colourDark!!,
        retailer.onColourDark!!,
        retailer.initialism!!,
        retailer.local!!
    )

    fun toRetailer(stores: List<Store>): Retailer {
        return Retailer(
            name,
            automated,
            verified,
            stores,
            colourLight,
            onColourLight,
            colourDark,
            onColourDark,
            initialism,
            local
        )
    }
}
