import com.example.cosc345.shared.models.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Tests for the methods on a product.
 */
@DisplayName("Product Tests")
class ProductTests {
    /**
     * Test if the [Product.getBestInformation] method returns the correct information.
     */
    @DisplayName("Test getting best information")
    @Test
    fun `Test getting best information`() {
        val product = Product(
            mutableListOf(
                RetailerProductInformation(
                    "new-world",
                    "7896",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345")
                ),
                RetailerProductInformation(
                    "countdown",
                    "1234",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345")
                )
            )
        )

        assert(product.getBestInformation().id == "1234")
    }

    /**
     * Test if the [Product.getBestLocalPrice] returns the best local price.
     */
    @Test
    @DisplayName("Check best local price")
    fun `Check best local price`() {
        val retailers = mapOf(
            "countdown" to Retailer(
                "Countdown",
                local = false,
                stores = listOf(
                    Store(
                        "test-countdown",
                        "Test"
                    )
                )
            ),
            "leckies-butchery" to Retailer(
                "Leckies Butchery",
                local = true,
                stores = listOf(
                    Store(
                        "test-leckies",
                        "Test"
                    )
                )
            )
        )

        val product = Product(
            mutableListOf(
                RetailerProductInformation(
                    "countdown",
                    "7896",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345"),
                    pricing = mutableListOf(
                        StorePricingInformation(
                            "test-countdown",
                            price = 100
                        )
                    )
                ),
                RetailerProductInformation(
                    "leckies-butchery",
                    "1234",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345"),
                    pricing = mutableListOf(
                        StorePricingInformation(
                            "test-leckies",
                            price = 200
                        )
                    )
                )
            )
        )

        assert(product.getBestLocalPrice(retailers)?.first?.contains("2") == true)
    }

    /**
     * Test if the [Product.getBestNonLocalPrice] gets the best non-local price.
     */
    @Test
    @DisplayName("Check best non-local price")
    fun `Check best non-local price`() {
        val retailers = mapOf(
            "countdown" to Retailer(
                "Countdown",
                local = false,
                stores = listOf(
                    Store(
                        "test-countdown",
                        "Test"
                    )
                )
            ),
            "leckies-butchery" to Retailer(
                "Leckies Butchery",
                local = true,
                stores = listOf(
                    Store(
                        "test-leckies",
                        "Test"
                    )
                )
            )
        )

        val product = Product(
            mutableListOf(
                RetailerProductInformation(
                    "countdown",
                    "7896",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345"),
                    pricing = mutableListOf(
                        StorePricingInformation(
                            "test-countdown",
                            price = 200
                        )
                    )
                ),
                RetailerProductInformation(
                    "leckies-butchery",
                    "1234",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345"),
                    pricing = mutableListOf(
                        StorePricingInformation(
                            "test-leckies",
                            price = 100
                        )
                    )
                )
            )
        )

        assert(product.getBestNonLocalPrice(retailers)?.first?.contains("2") == true)
    }

    /**
     * Test if the [Product.getBestNonLocalPrice] gets the best non-local price if there's no non-local retailer.
     */
    @Test
    @DisplayName("Check best non-local price if no non-local retailers")
    fun `Check best non-local price if no non-local retailers`() {
        val retailers = mapOf(
            "countdown" to Retailer(
                "Countdown",
                local = true,
                stores = listOf(
                    Store(
                        "test-countdown",
                        "Test"
                    )
                )
            ),
            "leckies-butchery" to Retailer(
                "Leckies Butchery",
                local = true,
                stores = listOf(
                    Store(
                        "test-leckies",
                        "Test"
                    )
                )
            )
        )

        val product = Product(
            mutableListOf(
                RetailerProductInformation(
                    "countdown",
                    "7896",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345"),
                    pricing = mutableListOf(
                        StorePricingInformation(
                            "test-countdown",
                            price = 200
                        )
                    )
                ),
                RetailerProductInformation(
                    "leckies-butchery",
                    "1234",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345"),
                    pricing = mutableListOf(
                        StorePricingInformation(
                            "test-leckies",
                            price = 100
                        )
                    )
                )
            )
        )

        assert(product.getBestNonLocalPrice(retailers) == null)
    }

    /**
     * Test if the [Product.getBestLocalPrice] returns the best local price if no local retailer.
     */
    @Test
    @DisplayName("Check best local price if no local retailer")
    fun `Check best local price if no local retailer`() {
        val retailers = mapOf(
            "countdown" to Retailer(
                "Countdown",
                local = false,
                stores = listOf(
                    Store(
                        "test-countdown",
                        "Test"
                    )
                )
            ),
            "leckies-butchery" to Retailer(
                "Leckies Butchery",
                local = false,
                stores = listOf(
                    Store(
                        "test-leckies",
                        "Test"
                    )
                )
            )
        )

        val product = Product(
            mutableListOf(
                RetailerProductInformation(
                    "countdown",
                    "7896",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345"),
                    pricing = mutableListOf(
                        StorePricingInformation(
                            "test-countdown",
                            price = 100
                        )
                    )
                ),
                RetailerProductInformation(
                    "leckies-butchery",
                    "1234",
                    "Name",
                    "Brand Name",
                    "Variant",
                    SaleType.EACH,
                    "100g",
                    100,
                    listOf("12345"),
                    pricing = mutableListOf(
                        StorePricingInformation(
                            "test-leckies",
                            price = 200
                        )
                    )
                )
            )
        )

        assert(product.getBestLocalPrice(retailers) == null)
    }
}