package com.example.cosc345.shared.models

/**
 * A retailer, which is the overarching brand for bigger stores, but the individual brand for smaller stores.
 *
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
     * Whether this retailer was added by the retailer themselves.
     */
    var verified: Boolean? = null,

    /**
     * An index of the stores associated with this retailer.
     */
    var stores: List<Store>? = null,

    /**
     * The colour associated with this store for light themes.
     */
    var colourLight: Long? = null,

    /**
     * The text colour to use for this store for light themes.
     */
    var onColourLight: Long? = null,

    /**
     * The colour associated with this store for dark themes.
     */
    var colourDark: Long? = null,

    /**
     * The text colour to uuse for this store for dark themes.
     */
    var onColourDark: Long? = null,

    /**
     * The two digit initialism to use for this store.
     */
    var initialism: String? = null,

    /**
     * Whether this retailer is local or not.
     */
    var local: Boolean? = null
)
