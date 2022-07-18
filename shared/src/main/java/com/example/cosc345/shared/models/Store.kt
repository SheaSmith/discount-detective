package com.example.cosc345.shared.models

/**
 * An individual store. For small stores, this will essentially be the same as the retailer, but for bigger ones it will specify the attributes of particular stores.
 */
data class Store(
    /**
     * The name of the store, for example, Centre City.
     */
    var name: String? = null,

    /**
     * The address of the store.
     */
    var address: String? = null,

    /**
     * The latitude of the store.
     */
    var latitude: Double? = null,

    /**
     * The longitude of the store.
     */
    var longitude: Double? = null,

    /**
     * Whether the store was automatically added by the scraper, rather than by the retailer themselves, or a user.
     */
    var automated: Boolean? = null
)
