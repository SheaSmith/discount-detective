package com.example.cosc345.scraper.models.foursquare.mailer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An individual mailer issue.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FourSquareMailer(
    /**
     * The ID of this issue.
     */
    @Json(name = "nid_1")
    val id: String
)
