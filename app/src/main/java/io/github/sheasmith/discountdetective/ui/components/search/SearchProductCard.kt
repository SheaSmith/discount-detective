package io.github.sheasmith.discountdetective.ui.components.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder
import io.github.sheasmith.discountdetective.R
import io.github.sheasmith.discountdetective.ui.components.MinimumHeightState
import io.github.sheasmith.discountdetective.ui.components.minimumHeightModifier
import io.github.sheasmith.discountdetective.ui.components.product.AddToShoppingListBlock
import io.github.sheasmith.discountdetective.ui.components.product.ProductTitle
import kotlinx.coroutines.CoroutineScope

/**
 * Product card function displays each product's information.
 *
 * Creates a section ("product card") for each product in the search screen, so that users can
 * easily differentiate between the information of each product and click on it to open the
 * corresponding product screen.
 *
 * @param productPair The product to display.
 * @param loading Boolean variable for whether or not the screen has finished loading.
 * @param navController The nav controller to use for navigating between views.
 * @param retailers A map of all retailers.
 * @param snackbarHostState The snackbar host for displaying snackbars.
 * @param coroutineScope The scope for launching suspend functions within the view.
 * @param region The region (city/town) that the user wants to compare products for.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchProductCard(
    productPair: Pair<String, Product>?,
    loading: Boolean,
    navController: NavHostController,
    retailers: Map<String, Retailer>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onAddToShoppingList: ((String, String, String, Double) -> Unit)? = null,
    region: String
) {
    val product = productPair?.second

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            disabledContainerColor = Color.Transparent,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            ElevatedCard(
                onClick = {
                    if (product != null) {
                        navController.navigate("products/${productPair.first}")
                    }
                },
                enabled = !loading,
                colors = CardDefaults.elevatedCardColors(
                    disabledContainerColor = Color.Transparent
                ),
                elevation = if (loading) CardDefaults.cardElevation() else CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Contents(
                        product = product, retailers = retailers, loading = loading,
                        region = region
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                AddToShoppingListBlock(
                    snackbarHostState,
                    productPair,
                    retailers,
                    loading,
                    onAddToShoppingList,
                    coroutineScope,
                    region
                )
            }
        }
    }
}

@Composable
private fun Contents(
    product: Product?,
    retailers: Map<String, Retailer>,
    region: String,
    loading: Boolean
) {
    val info = product?.getBestInformation()
    val bestLocalPrice = product?.getBestLocalPrice(retailers, region)
    val bestNonLocalPrice = product?.getBestNonLocalPrice(retailers, region)

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (info?.image != null || loading) {
            AsyncImage(
                model = info?.image,
                contentDescription = stringResource(id = R.string.content_description_product_image),
                modifier = Modifier
                    .fillMaxHeight()
                    .height(100.dp)
                    .width(100.dp)
                    .align(Alignment.Top)
                    .placeholder(
                        visible = loading,
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Top)
        ) {
            ProductTitle(info = info, loading = loading)

            FlowRow(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 4.dp
            ) {

                val density = LocalDensity.current

                val minimumHeightState = remember { MinimumHeightState() }
                val minimumHeightStateModifier = Modifier.minimumHeightModifier(
                    minimumHeightState, density
                )

                SearchProductPricingBlock(
                    components = bestNonLocalPrice,
                    loading = loading,
                    local = false,
                    modifier = minimumHeightStateModifier
                )

                SearchProductPricingBlock(
                    components = bestLocalPrice,
                    loading = loading,
                    local = true,
                    modifier = minimumHeightStateModifier
                )
            }

        }
    }
}