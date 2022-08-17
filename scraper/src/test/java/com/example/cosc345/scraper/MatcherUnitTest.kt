package com.example.cosc345.scraper

import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Matcher tests")
class MatcherUnitTest {
    /**
     * Test for issue #26
     */
    @Test
    @DisplayName("Test Royal Gala Apples example")
    fun `Test matcher`() {
        val products = mapOf(
            "9342446099745" to Product(
                mutableListOf(
                    RetailerProductInformation(
                        "countdown",
                        "9342446099745",
                        "Apples",
                        "Fresh Produce",
                        "Royal Gala",
                        SaleType.WEIGHT,
                        null,
                        1000,
                        listOf("9342446099745"),
                        "https://assets.woolworths.com.au/images/2010/155003.jpg?impolicy=wowcdxwbjbx&w=200&h=200",
                        mutableListOf(),
                        true,
                        false
                    )
                )
            ),
            "10000002041732" to Product(
                mutableListOf(
                    RetailerProductInformation(
                        "new-world",
                        "5046525-KGM-000",
                        "Royal Gala Apples",
                        null,
                        null,
                        SaleType.WEIGHT,
                        null,
                        1000,
                        listOf("10000002041732", "20041731", "204173000007", "9400989469644"),
                        "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                        mutableListOf(),
                        true,
                        false
                    ),
                    RetailerProductInformation(
                        "paknsave",
                        "5046525-KGM-000",
                        "Royal Gala Apples",
                        null,
                        null,
                        SaleType.WEIGHT,
                        null,
                        1000,
                        listOf("10000002041732", "20041731", "204173000007", "9400989469644"),
                        "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                        mutableListOf(),
                        true,
                        false
                    )
                )
            ),
            "74173" to Product(
                mutableListOf(
                    RetailerProductInformation(
                        "freshchoice",
                        "74173",
                        "Apples - Royal Gala",
                        null,
                        null,
                        SaleType.WEIGHT,
                        null,
                        1000,
                        listOf("74173"),
                        "https://dtgxwmigmg3gc.cloudfront.net/imagery/assets/derivations/icon/64/64/true/eyJpZCI6IjExMWZlNTM2Y2IxNTAwOGY4MThhOWQ1ZWNiYTgyMTMzIiwic3RvcmFnZSI6InB1YmxpY19zdG9yZSJ9?signature=91c44e23792d9bd2d0f5b3d24bcefe681b578ed9e8200bd5026d9c326ac62c70",
                        mutableListOf(),
                        true,
                        false
                    )
                )
            )
        )

        val matcher = Matcher()

        val result = matcher.matchNames(products, mapOf())

        assert(result.second.size == 1)
    }

    /**
     * Test for issue #27
     */
    @Test
    @DisplayName("Test Coconut plural matching")
    fun `Test Coconut plural matching`() {
        val products = mapOf(
            "7-veggie-boys" to Product(
                mutableListOf(
                    RetailerProductInformation(
                        "veggie-boys",
                        "7",
                        "Coconuts",
                        null,
                        null,
                        SaleType.EACH,
                        null,
                        1000,
                        null,
                        "https://veggieboys.co.nz/upload/product/coconut_whole.jpg",
                        mutableListOf(),
                        true,
                        false
                    )
                )
            ),
            "205323000007" to Product(
                mutableListOf(
                    RetailerProductInformation(
                        "new-world",
                        "5275423-EA-000",
                        "Coconut",
                        null,
                        null,
                        SaleType.EACH,
                        "1ea",
                        null,
                        listOf("205323000007", "9400989352120"),
                        "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5275423.png",
                        mutableListOf(),
                        true,
                        false
                    ),
                    RetailerProductInformation(
                        "paknsave",
                        "5275423-EA-000",
                        "Coconut",
                        null,
                        null,
                        SaleType.EACH,
                        "1ea",
                        null,
                        listOf("205323000007", "9400989352120"),
                        "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5275423.png",
                        mutableListOf(),
                        true,
                        false
                    )
                )
            ),
            "74261" to Product(
                mutableListOf(
                    RetailerProductInformation(
                        "freshchoice",
                        "74261",
                        "Coconut",
                        null,
                        null,
                        SaleType.EACH,
                        null,
                        null,
                        listOf("74261"),
                        "https://dtgxwmigmg3gc.cloudfront.net/imagery/assets/derivations/icon/64/64/true/eyJpZCI6IjljMjk0Yzk0Nzk3YjZhYjk5MGNiZWY5MjY4MzFkM2NlIiwic3RvcmFnZSI6InB1YmxpY19zdG9yZSJ9?signature=29142cc38bda07b459055aff90503d93774e9c4c3bbc8060c1b11576fda7b815",
                        mutableListOf(),
                        true,
                        false
                    )
                )
            ),
            "9421904769960" to Product(
                mutableListOf(
                    RetailerProductInformation(
                        "countdown",
                        "136981",
                        "Coconut",
                        "Fresh Produce",
                        "Imported",
                        SaleType.EACH,
                        null,
                        null,
                        listOf("9421904769960"),
                        "https://assets.woolworths.com.au/images/2010/136981.jpg?impolicy=wowcdxwbjbx&w=200&h=200",
                        mutableListOf(),
                        true,
                        false
                    )
                )
            )
        )

        val matcher = Matcher()

        val result = matcher.matchNames(products, mapOf())

        assert(result.second.size == 1)
    }
}