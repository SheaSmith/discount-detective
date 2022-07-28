package com.example.cosc345.scraper.models.countdown.departments

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A department/category used by Countdown to organise products.
 *
 * @author Shea Smith
 * @constructor Create an instance of this object. This should only be used by the automatic Moshi parser.
 */
@JsonClass(generateAdapter = true)
data class CountdownDepartment(
    /**
     * The URL slug for this category.
     */
    @Json(name = "url")
    val url: String
)
