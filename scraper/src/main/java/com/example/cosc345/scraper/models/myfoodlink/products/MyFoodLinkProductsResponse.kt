package com.example.cosc345.scraper.models.myfoodlink.products

import com.example.cosc345.scraper.converter.MyFoodLinkGtmDataConverter
import pl.droidsonroids.jspoon.annotation.Selector

data class MyFoodLinkProductsResponse(
    @Selector(".line")
    var lines: List<MyFoodLinkLine>? = null,

    @Selector("head > script:nth-of-type(8)", converter = MyFoodLinkGtmDataConverter::class)
    var gtmData: Array<MyFoodLinkGtmData>? = null,

    // https://stackoverflow.com/questions/5418744/select-second-last-element-with-css
    @Selector(".mfl-pagination a:nth-last-child(2)")
    var pages: Int? = null
) {
    // Kotlin boilerplate
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

    override fun hashCode(): Int {
        var result = lines?.hashCode() ?: 0
        result = 31 * result + (gtmData?.contentHashCode() ?: 0)
        result = 31 * result + (pages ?: 0)
        return result
    }

}
