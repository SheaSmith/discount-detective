import com.example.cosc345.shared.extensions.capitaliseNZ
import com.example.cosc345.shared.extensions.titleCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Tests for the string extensions.
 */
@DisplayName("String tests")
class StringTests {
    /**
     * Test if the title case extension works.
     */
    @DisplayName("Test title case")
    @Test
    fun `Test title case`() {
        assert("tHis sHouLd Be tItle CASE".titleCase() == "THis SHouLd Be TItle CASE")
    }

    /**
     * Test if the capitalisation of NZ extension works.
     */
    @DisplayName("Test capitalise NZ")
    @Test
    fun `Test capitalise NZ`() {
        assert("nz this is nz again nZ nz".capitaliseNZ() == "NZ this is NZ again NZ NZ")
    }
}