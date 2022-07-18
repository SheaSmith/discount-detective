package com.example.cosc345.shared.models

/**
 * A retailer, which is the overarching brand for bigger stores, but the individual brand for smaller stores.
 */
data class Retailer(
    /**
     * The name of the retailer, for example Countdown.
     */
    var name: String? = null,

    /**
     * Whether the retailer was automatically added by the scraper, rather than by the retailer themselves, or a user.
     */
    var automated: Boolean? = null
)
