package com.example.cosc345.shared.models

/**
 * A retailer, which is the overarching brand for bigger stores, but the individual brand for smaller stores.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. Some of the nullable parameters are not nullable in practice, but are required to be for Firebase.
 */
data class Retailer(
    /**
     * The name of the retailer, for example Countdown.
     */
    var name: String? = null,

    /**
     * Whether the retailer was automatically added by the scraper, rather than by the retailer themselves, or a user.
     */
    var automated: Boolean? = null,

    /**
     * An index of the stores associated with this retailer.
     */
    var stores: List<Store>? = null,
)
