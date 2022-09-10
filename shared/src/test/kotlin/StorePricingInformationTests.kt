import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345.shared.models.StorePricingInformation
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Test if the store pricing information methods work correctly.
 */
@DisplayName("Store pricing information")
class StorePricingInformationTests {
    /**
     * Check if the correct display price is returned by [StorePricingInformation.getDisplayPrice] with only one argument
     */
    @DisplayName("Check display price")
    @Test
    fun `Check display price`() {
        val retailerProductInformation = RetailerProductInformation(
            "test",
            saleType = SaleType.EACH,
            pricing = mutableListOf(
                StorePricingInformation(
                    "test",
                    price = 100
                )
            )
        )

        val pricingInformation = retailerProductInformation.pricing!!.first()

        assert(
            pricingInformation.getDisplayPrice(retailerProductInformation) == Pair(
                "$1",
                ".00/ea"
            )
        )
    }

    /**
     * Check if the correct result is returned by [StorePricingInformation.getDisplayPrice] with one argument and a weight specified.
     */
    @DisplayName("Check display price weight")
    @Test
    fun `Check display price weight`() {
        val retailerProductInformation = RetailerProductInformation(
            "test",
            saleType = SaleType.WEIGHT,
            weight = 2000,
            pricing = mutableListOf(
                StorePricingInformation(
                    "test",
                    discountPrice = 200,
                    price = 400
                )
            )
        )

        val pricingInformation = retailerProductInformation.pricing!!.first()

        assert(
            pricingInformation.getDisplayPrice(retailerProductInformation) == Pair(
                "$1",
                ".00/kg"
            )
        )
    }

    /**
     * Check if [StorePricingInformation.getDisplayPrice] returns the correct price with a manual input and a weight.
     */
    @DisplayName("Check display price weight manual input")
    @Test
    fun `Check display price weight manual input`() {
        val retailerProductInformation = RetailerProductInformation(
            "test",
            saleType = SaleType.WEIGHT,
            weight = 2000,
            pricing = mutableListOf(
                StorePricingInformation(
                    "test",
                    discountPrice = 200,
                    price = 400
                )
            )
        )

        val pricingInformation = retailerProductInformation.pricing!!.first()

        assert(
            pricingInformation.getDisplayPrice(retailerProductInformation, 100) == Pair(
                "$1",
                ".00/kg"
            )
        )
    }

    /**
     * Check if [StorePricingInformation.getDisplayPrice] returns the correct price with a manual input.
     */
    @DisplayName("Check display price manual input")
    @Test
    fun `Check display price manual input`() {
        val retailerProductInformation = RetailerProductInformation(
            "test",
            saleType = SaleType.EACH,
            pricing = mutableListOf(
                StorePricingInformation(
                    "test",
                    price = 100
                )
            )
        )

        val pricingInformation = retailerProductInformation.pricing!!.first()

        assert(
            pricingInformation.getDisplayPrice(retailerProductInformation, 100) == Pair(
                "$1",
                ".00/ea"
            )
        )
    }
}