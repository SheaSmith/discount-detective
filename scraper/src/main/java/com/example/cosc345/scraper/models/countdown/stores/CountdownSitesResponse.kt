package com.example.cosc345.scraper.models.countdown.stores

import com.squareup.moshi.Json

data class CountdownSitesResponse(
    @Json(name = "siteDetail")
    val siteDetails: Array<CountdownSiteDetail>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CountdownSitesResponse

        if (!siteDetails.contentEquals(other.siteDetails)) return false

        return true
    }

    override fun hashCode(): Int {
        return siteDetails.contentHashCode()
    }
}
