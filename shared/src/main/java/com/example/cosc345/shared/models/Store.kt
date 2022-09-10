package com.example.cosc345.shared.models

/**
 * An individual store. For small stores, this will essentially be the same as the retailer, but for bigger ones it will specify the attributes of particular stores.
 *
 * @constructor Create a new instance of this object. Some of the nullable parameters are not nullable in practice, but are required to be for Firebase.
 */
data class Store(
    /**
     * The unique ID for this store.
     *
     * Required.
     */
    var id: String? = null,

    /**
     * The name of the store, for example, Centre City.
     *
     * Required.
     */
    var name: String? = null,

    /**
     * The address of the store.
     *
     * Required.
     */
    var address: String? = null,

    /**
     * The latitude of the store.
     *
     * Required.
     */
    var latitude: Double? = null,

    /**
     * The longitude of the store.
     *
     * Required.
     */
    var longitude: Double? = null,

    /**
     * Whether the store was automatically added by the scraper, rather than by the retailer themselves, or a user.
     *
     * Required.
     */
    var automated: Boolean? = null,

    /**
     * The region of this specific store. Required.
     */
    var region: String? = null
)
