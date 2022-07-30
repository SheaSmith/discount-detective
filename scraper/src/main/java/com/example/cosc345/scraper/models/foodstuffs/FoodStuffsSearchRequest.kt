package com.example.cosc345.scraper.models.foodstuffs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A request to any search endpoint for FoodStuffs.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. This should only be used by Moshi.
 */
@JsonClass(generateAdapter = true)
data class FoodStuffsSearchRequest(
    /**
     * The search query to use.
     */
    @Json(name = "query")
    val query: String = "",

    /**
     * How many values should be returned per category. As we only look at once category at a time, this is not relevant.
     */
    @Json(name = "maxValuesPerFacet")
    val maxValuesPerFacet: Int = 1000,

    /**
     * How many results should be returned. The maximum is 1000.
     */
    @Json(name = "hitsPerPage")
    val hitsPerPage: Int = 1000,

    /**
     * Filters to use to limit the number of products returned.
     */
    @Json(name = "facetFilters")
    val facetFilters: Array<Array<String>>
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
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

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = query.hashCode()
        result = 31 * result + maxValuesPerFacet
        result = 31 * result + hitsPerPage
        result = 31 * result + facetFilters.contentDeepHashCode()
        return result
    }
}
