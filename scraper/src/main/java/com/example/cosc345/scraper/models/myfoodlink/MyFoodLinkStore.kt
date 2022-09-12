package com.example.cosc345.scraper.models.myfoodlink

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The model for a particular store for a MyFoodLink based retailer.
 *
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class MyFoodLinkStore(
    /**
     * The domain name for a specific MyFoodLink store, for example, roslyn.store.freshchoice.co.nz.
     */
    @Json(name = "MDOMAIN")
    val hostname: String?,

    /**
     * The name of the store, for example FreshChoice Roslyn.
     */
    @Json(name = "MSHOPNAME")
    val name: String?,

    /**
     * The unique shop number for this store.
     */
    @Json(name = "MSHOPNUM")
    val id: String?,

    /**
     * The type of store website this is. We are interested in the type of "ecommerce", as the others are just basic websites without online stores.
     */
    @Json(name = "type")
    val type: String?
)
