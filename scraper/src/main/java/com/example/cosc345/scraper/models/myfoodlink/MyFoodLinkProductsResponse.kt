package com.example.cosc345.scraper.models.myfoodlink

import com.example.cosc345.scraper.converter.MyFoodLinkGtmDataConverter
import pl.droidsonroids.jspoon.annotation.Selector

data class MyFoodLinkProductsResponse(
    @Selector(".line")
    val lines: List<MyFoodLinkLine>,

    @Selector(
        "script",
        regex = "\\n*\\s*window.gtmDataLayer\\s=\\s[^;]+",
        converter = MyFoodLinkGtmDataConverter::class
    )
    val gtmData: Array<MyFoodLinkGtmData>,

    // https://stackoverflow.com/questions/5418744/select-second-last-element-with-css
    @Selector(".mfl-pagination:nth-last-child(2)")
    val pages: Int
) {
    // Kotlin boilerplate
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyFoodLinkProductsResponse

        if (lines != other.lines) return false
        if (!gtmData.contentEquals(other.gtmData)) return false
        if (pages != other.pages) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lines.hashCode()
        result = 31 * result + gtmData.contentHashCode()
        result = 31 * result + pages
        return result
    }
}
