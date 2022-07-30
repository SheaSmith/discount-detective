package com.example.cosc345.scraper.models.countdown.stores

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The store detail object, which contains the actual information about the store (and also opening hours in the JSON, but we don't look at this).
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class CountdownSiteDetail(
    /**
     * The information about the store.
     */
    @Json(name = "site")
    val site: CountdownSite
)
