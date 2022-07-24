package com.example.cosc345.scraper.models.wooCom

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class wooComVariants (
    @Json(name = "id")
    val id: String?,

    @Json(name = "attributes")
    val attributes: List<wooComAttributes>

) {
    /**
     * Attributes container
     */
    data class wooComAttributes (
        @Json(name = "name")
        val id: String?,

        @Json(name = "value")
        val value: String?
    )
}
