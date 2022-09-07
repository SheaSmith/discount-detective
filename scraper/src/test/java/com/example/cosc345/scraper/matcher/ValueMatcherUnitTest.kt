package com.example.cosc345.scraper.matcher

import com.example.cosc345.scraper.Matcher
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Value matcher tests")
class ValueMatcherUnitTest {
    @Test
    @DisplayName("Multi-value test")
    fun `Multi-barcode test`() {
        val retailerProductInfos = mutableListOf<RetailerProductInformation>()
        val repeat = 1000

        repeat(repeat) {
            retailerProductInfos.addAll(
                listOf(
                    RetailerProductInformation(
                        "countdown",
                        "9342446099745$it",
                        "Apples$it",
                        "Fresh Produce",
                        "Royal Gala",
                        SaleType.WEIGHT,
                        null,
                        1000,
                        listOf("123456789$it", "987654321$it"),
                        "https://assets.woolworths.com.au/images/2010/155003.jpg?impolicy=wowcdxwbjbx&w=200&h=200",
                        mutableListOf(),
                        true,
                        false
                    ),
                    RetailerProductInformation(
                        "new-world",
                        "5046525-KGM-000$it",
                        "Royal Gala Apples$it",
                        null,
                        null,
                        SaleType.WEIGHT,
                        null,
                        1000,
                        listOf("987654321$it"),
                        "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                        mutableListOf(),
                        true,
                        false
                    ),
                    RetailerProductInformation(
                        "paknsave",
                        "5046525-KGM-000$it",
                        "Royal Gala Apples$it",
                        null,
                        null,
                        SaleType.WEIGHT,
                        null,
                        1000,
                        listOf("987654321$it"),
                        "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                        mutableListOf(),
                        true,
                        false
                    ),
                    RetailerProductInformation(
                        "freshchoice",
                        "74173$it",
                        "Apples$it - Royal Gala",
                        null,
                        null,
                        SaleType.WEIGHT,
                        null,
                        1000,
                        listOf("123456789$it"),
                        "https://dtgxwmigmg3gc.cloudfront.net/imagery/assets/derivations/icon/64/64/true/eyJpZCI6IjExMWZlNTM2Y2IxNTAwOGY4MThhOWQ1ZWNiYTgyMTMzIiwic3RvcmFnZSI6InB1YmxpY19zdG9yZSJ9?signature=91c44e23792d9bd2d0f5b3d24bcefe681b578ed9e8200bd5026d9c326ac62c70",
                        mutableListOf(),
                        true,
                        false
                    )
                )
            )
        }

        val results = Matcher().matchNames(retailerProductInfos.associate {
            it.barcodes!!.first() to Product(mutableListOf(it))
        }, mapOf())

        assert(results.second.size == repeat)
    }

    @Test
    @DisplayName("Different products")
    fun `Different products`() {
        val infos = mutableListOf(
            RetailerProductInformation(
                "countdown",
                "9342446099745",
                "Applesa",
                "Fresh Produce",
                "Royal Gala",
                SaleType.WEIGHT,
                null,
                1000,
                listOf("1234567899", "9876543214"),
                "https://assets.woolworths.com.au/images/2010/155003.jpg?impolicy=wowcdxwbjbx&w=200&h=200",
                mutableListOf(),
                true,
                false
            ),
            RetailerProductInformation(
                "new-world",
                "5046525-KGM-000",
                "Royal Gala Apples",
                null,
                null,
                SaleType.WEIGHT,
                null,
                1000,
                listOf(),
                "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                mutableListOf(),
                true,
                false
            ),
            RetailerProductInformation(
                "paknsave",
                "5046525-KGM-000",
                "Royal Gala Applesa",
                null,
                null,
                SaleType.WEIGHT,
                null,
                1000,
                listOf("9876543217"),
                "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                mutableListOf(),
                true,
                false
            ),
            RetailerProductInformation(
                "freshchoice",
                "74173t",
                "Apples - Royal Gala",
                null,
                null,
                SaleType.WEIGHT,
                null,
                1000,
                null,
                "https://dtgxwmigmg3gc.cloudfront.net/imagery/assets/derivations/icon/64/64/true/eyJpZCI6IjExMWZlNTM2Y2IxNTAwOGY4MThhOWQ1ZWNiYTgyMTMzIiwic3RvcmFnZSI6InB1YmxpY19zdG9yZSJ9?signature=91c44e23792d9bd2d0f5b3d24bcefe681b578ed9e8200bd5026d9c326ac62c70",
                mutableListOf(),
                true,
                false
            ),
            RetailerProductInformation(
                "freshchoice2",
                "74173tz",
                "Applesz - Royal Gala",
                null,
                null,
                SaleType.WEIGHT,
                null,
                1000,
                null,
                "https://dtgxwmigmg3gc.cloudfront.net/imagery/assets/derivations/icon/64/64/true/eyJpZCI6IjExMWZlNTM2Y2IxNTAwOGY4MThhOWQ1ZWNiYTgyMTMzIiwic3RvcmFnZSI6InB1YmxpY19zdG9yZSJ9?signature=91c44e23792d9bd2d0f5b3d24bcefe681b578ed9e8200bd5026d9c326ac62c70",
                mutableListOf(),
                true,
                false
            )
        )

        val results =
            Matcher().matchNames(infos.associate { it.id!! to Product(mutableListOf(it)) }, mapOf())

        assert(results.second.size == 3)
    }

    @Test
    @DisplayName("Retailer with two barcodes")
    fun `Retailer with two similar matches`() {
        val infos = mutableListOf(
            RetailerProductInformation(
                "countdown",
                "9342446099745",
                "Apples",
                "Fresh Produce",
                "Royal Gala",
                SaleType.WEIGHT,
                null,
                1000,
                listOf("123456789", "987654321"),
                "https://assets.woolworths.com.au/images/2010/155003.jpg?impolicy=wowcdxwbjbx&w=200&h=200",
                mutableListOf(),
                true,
                false
            ),
            RetailerProductInformation(
                "new-world",
                "5046525-KGM-000",
                "Apples",
                null,
                "Royal Gala",
                SaleType.WEIGHT,
                null,
                1000,
                listOf(),
                "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                mutableListOf(),
                true,
                false
            ),
            RetailerProductInformation(
                "new-world",
                "504655-KGM-000",
                "Apples",
                null,
                "Different Type",
                SaleType.WEIGHT,
                null,
                1000,
                listOf(),
                "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                mutableListOf(),
                true,
                false
            )
        )

        val results =
            Matcher().matchNames(infos.associate { it.id!! to Product(mutableListOf(it)) }, mapOf())

        assert(results.second.size == 2)
    }
}