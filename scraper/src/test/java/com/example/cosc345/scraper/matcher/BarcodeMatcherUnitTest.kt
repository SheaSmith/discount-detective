package com.example.cosc345.scraper.matcher

import com.example.cosc345.scraper.Matcher
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Barcode matcher tests")
class BarcodeMatcherUnitTest {
    @Test
    @DisplayName("Multi-barcode test")
    fun `Multi-barcode test`() {
        val retailerProductInfos = mutableListOf<RetailerProductInformation>()
        val repeat = 1000

        repeat(repeat) {
            retailerProductInfos.addAll(
                listOf(
                    RetailerProductInformation(
                        "countdown",
                        "9342446099745$it",
                        "Apples",
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
                        "Royal Gala Apples",
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
                        "Royal Gala Apples",
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
                        "Apples - Royal Gala",
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

        val results = Matcher().matchBarcodes(retailerProductInfos, mapOf())

        assert(results.second.size == repeat)
    }

    @Test
    @DisplayName("Products with missing barcodes")
    fun `Products with missing barcodes`() {
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
                "Royal Gala Apples",
                null,
                null,
                SaleType.WEIGHT,
                null,
                1000,
                listOf("987654321"),
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
            )
        )

        val results = Matcher().matchBarcodes(infos, mapOf())

        assert(results.second.size == 3)
    }

    @Test
    @DisplayName("Retailer with two barcodes")
    fun `Retailer with two barcodes`() {
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
                "Royal Gala Apples",
                null,
                null,
                SaleType.WEIGHT,
                null,
                1000,
                listOf("123456789"),
                "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
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
                listOf("987654321"),
                "https://a.fsimg.co.nz/product/retail/fan/image/500x500/5046525.png",
                mutableListOf(),
                true,
                false
            )
        )

        val results = Matcher().matchBarcodes(infos, mapOf())

        assert(results.second.size == 2)
    }
}