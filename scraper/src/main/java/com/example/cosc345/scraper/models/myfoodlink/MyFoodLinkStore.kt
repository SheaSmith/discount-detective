package com.example.cosc345.scraper.models.myfoodlink

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyFoodLinkStore(
    @Json(name = "MDOMAIN")
    val hostname: String?,

    @Json(name = "MSHOPNAME")
    val name: String?,

    @Json(name = "MSHOPNUM")
    val id: String?,

    @Json(name = "type")
    val type: String?
)
