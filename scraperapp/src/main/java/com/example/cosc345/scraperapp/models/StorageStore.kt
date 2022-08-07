package com.example.cosc345.scraperapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.cosc345.shared.models.Store

@Entity(
    tableName = "Stores",
    primaryKeys = ["storeRetailer", "storeId"]
)
data class StorageStore(
    @ColumnInfo(name = "storeRetailer")
    val retailer: String,

    /**
     * The unique ID for this store.
     *
     * Required.
     */
    @ColumnInfo(name = "storeId")
    val id: String,

    /**
     * The name of the store, for example, Centre City.
     *
     * Required.
     */
    @ColumnInfo(name = "storeName")
    val name: String,

    /**
     * The address of the store.
     *
     * Required.
     */
    @ColumnInfo(name = "storeAddress")
    val address: String?,

    /**
     * The latitude of the store.
     *
     * Required.
     */
    @ColumnInfo(name = "storeLatitude")
    val latitude: Double?,

    /**
     * The longitude of the store.
     *
     * Required.
     */
    @ColumnInfo(name = "storeLongitude")
    val longitude: Double?,

    /**
     * Whether the store was automatically added by the scraper, rather than by the retailer themselves, or a user.
     *
     * Required.
     */
    @ColumnInfo(name = "storeAutomated")
    val automated: Boolean
) {
    constructor(store: Store, retailer: String) : this(
        retailer,
        store.id!!,
        store.name!!,
        store.address,
        store.latitude,
        store.longitude,
        store.automated!!
    )

    fun toStore(): Store {
        return Store(
            id,
            name,
            address,
            latitude,
            longitude,
            automated
        )
    }
}