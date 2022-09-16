package com.example.cosc345.scraperapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.Store

/**
 * StorageRetailer is a version of [Retailer] that allows it to be saved in our local Room database, rather than Firebase.
 */
@Entity(tableName = "Retailers")
data class StorageRetailer(
    /**
     * The unique ID of the retailer.
     */
    @PrimaryKey
    val id: String,

    /**
     * The name of the retailer, for example Countdown.
     */
    val name: String,

    /**
     * Whether the retailer was automatically added by the scraper, rather than by the retailer themselves, or a user.
     */
    val automated: Boolean,

    /**
     * Whether this retailer was added by the retailer themselves.
     */
    val verified: Boolean,

    /**
     * The colour associated with this store for light themes.
     */
    val colourLight: Long,

    /**
     * The text colour to use for this store for light themes.
     */
    val onColourLight: Long,

    /**
     * The text colour to uuse for this store for dark themes.
     */
    val onColourDark: Long,

    /**
     * The colour associated with this store for dark themes.
     */
    val colourDark: Long,

    /**
     * The two digit initialism to use for this store.
     */
    val initialism: String,

    /**
     * Whether this retailer is local or not.
     */
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

    /**
     * Convert this back to a normal [Retailer] for saving in Firebase, or for processing.
     *
     * @param stores A list of stores associated with this retailer.
     * @return An instance of the shared [Retailer] class, with the same information as this object.
     */
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
