package com.example.cosc345.scraper.models.foodstuffs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodStuffsSearchRequest(
    @Json(name = "query")
    val query: String = "",

    @Json(name = "maxValuesPerFacet")
    val maxValuesPerFacet: Int = 1000,

    @Json(name = "hitsPerPage")
    val hitsPerPage: Int = 1000,

    @Json(name = "facetFilters")
    val facetFilters: Array<Array<String>>
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodStuffsSearchRequest

        if (query != other.query) return false
        if (maxValuesPerFacet != other.maxValuesPerFacet) return false
        if (hitsPerPage != other.hitsPerPage) return false
        if (!facetFilters.contentDeepEquals(other.facetFilters)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = query.hashCode()
        result = 31 * result + maxValuesPerFacet
        result = 31 * result + hitsPerPage
        result = 31 * result + facetFilters.contentDeepHashCode()
        return result
    }
}
