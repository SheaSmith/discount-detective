import com.example.cosc345.shared.models.RetailerProductInformation
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Tests for the retailer product info.
 */
@DisplayName("Retailer product info tests")
class RetailerProductInformationTests {
    /**
     * Check if the correct retailer score is returned by [RetailerProductInformation.getRetailerScore]
     */
    @DisplayName("Check retailer score")
    @Test
    fun `Check retailer score`() {
        var retailerProductInformation = RetailerProductInformation(
            "countdown"
        )
        assert(retailerProductInformation.getRetailerScore() == 0)

        retailerProductInformation = RetailerProductInformation(
            "new-world"
        )
        assert(retailerProductInformation.getRetailerScore() == 1)

        retailerProductInformation = RetailerProductInformation(
            "paknsave"
        )
        assert(retailerProductInformation.getRetailerScore() == 1)

        retailerProductInformation = RetailerProductInformation(
            "freshchoice"
        )
        assert(retailerProductInformation.getRetailerScore() == 2)

        retailerProductInformation = RetailerProductInformation(
            "supervalue"
        )
        assert(retailerProductInformation.getRetailerScore() == 2)

        retailerProductInformation = RetailerProductInformation(
            "warehouse"
        )
        assert(retailerProductInformation.getRetailerScore() == 2)

        retailerProductInformation = RetailerProductInformation(
            "four-square"
        )
        assert(retailerProductInformation.getRetailerScore() == 4)

        retailerProductInformation = RetailerProductInformation(
            "four-squaregfdgegvfd"
        )
        assert(retailerProductInformation.getRetailerScore() == 3)
    }
}