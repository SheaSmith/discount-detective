package com.example.cosc345.scraperapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.cosc345.shared.models.Store

/**
 * A version of the normal [Store] that can be saved in a local Room database.
 */
@Entity(
    tableName = "Stores",
    primaryKeys = ["storeRetailer", "storeId"]
)
data class StorageStore(
    /**
     * The ID of the retailer associated with this store.
     */
    @ColumnInfo(name = "storeRetailer")
    val retailer: String,

    /**
     * The unique ID for this store.
     */
    @ColumnInfo(name = "storeId")
    val id: String,

    /**
     * The name of the store, for example, Centre City.
     */
    @ColumnInfo(name = "storeName")
    val name: String,

    /**
     * The address of the store.
     */
    @ColumnInfo(name = "storeAddress")
    val address: String?,

    /**
     * The latitude of the store.
     */
    @ColumnInfo(name = "storeLatitude")
    val latitude: Double?,

    /**
     * The longitude of the store.
     */
    @ColumnInfo(name = "storeLongitude")
    val longitude: Double?,

    /**
     * Whether the store was automatically added by the scraper, rather than by the retailer
     * themselves, or a user.
     */
    @ColumnInfo(name = "storeAutomated")
    val automated: Boolean,

    /**
     * The region of this specific store. Required.
     */
    var region: String
) {
    constructor(store: Store, retailer: String) : this(
        retailer,
        store.id!!,
        store.name!!,
        store.address,
        store.latitude,
        store.longitude,
        store.automated!!,
        store.region!!
    )

    /**
     * Convert this back to a normal [Store] for saving in Firebase, or for processing.
     *
     * @return An instance of the shared [Store] class, with the same information as this object.
     */
    fun toStore(): Store {
        return Store(
            id,
            name,
            address,
            latitude,
            longitude,
            automated,
            region
        )
    }
}