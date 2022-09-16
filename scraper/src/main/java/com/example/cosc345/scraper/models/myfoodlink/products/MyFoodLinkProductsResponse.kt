package com.example.cosc345.scraper.models.myfoodlink.products

import com.example.cosc345.scraper.converter.MyFoodLinkGtmDataConverter
import pl.droidsonroids.jspoon.annotation.Selector

/**
 * A response for the MyFoodLink products request.
 *
 * @constructor Create a new instance of this object. This should only be used by Jspoon.
 */
data class MyFoodLinkProductsResponse(
    /**
     * A list of all of the products.
     */
    @Selector(".line")
    var lines: List<MyFoodLinkLine>? = null,

    /**
     * Analytics data, including a list of products to cross reference.
     */
    @Selector("head > script:nth-of-type(8)", converter = MyFoodLinkGtmDataConverter::class)
    var gtmData: Array<MyFoodLinkGtmData>? = null,

    /**
     * The number of pages we can request.
     */
    // https://stackoverflow.com/questions/5418744/select-second-last-element-with-css
    @Selector(".mfl-pagination :nth-last-child(2)")
    var pages: String? = null
) {
    /**
     * Whether this object equals another, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyFoodLinkProductsResponse

        if (lines != other.lines) return false
        if (gtmData != null) {
            if (other.gtmData == null) return false
            if (!gtmData.contentEquals(other.gtmData)) return false
        } else if (other.gtmData != null) return false
        if (pages != other.pages) return false

        return true
    }

    /**
     * Generates a hashcode for this class, taking into account the array. This is boilerplate suggested by Kotlin.
     */
    override fun hashCode(): Int {
        var result = lines?.hashCode() ?: 0
        result = 31 * result + (gtmData?.contentHashCode() ?: 0)
        result = 31 * result + (pages?.hashCode() ?: 0)
        return result
    }

}
