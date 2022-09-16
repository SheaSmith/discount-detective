package io.github.sheasmith.discountdetective.ui.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder
import io.github.sheasmith.discountdetective.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A block that contains the buttons to add a specified product to the shopping list.
 *
 * @param snackbarHostState The snackbar host to allow the dialog to send snackbar messages.
 * @param productPair The pair of product to use, with the key being the ID and the value being the product.
 * @param retailers A map of all retailers, with the retailer ID as the key and the retailer as the value.
 * @param loading Whether the parent view is loading or not.
 * @param coroutineScope The coroutine scope for running suspend functions.
 */
@Composable
fun AddToShoppingListBlock(
    snackbarHostState: SnackbarHostState,
    productPair: Pair<String, Product>?,
    retailers: Map<String, Retailer>?,
    loading: Boolean,
    onAddToShoppingList: ((String, String, String, Int) -> Unit)?,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    region: String
) {
    var quantity by remember {
        mutableStateOf<Int?>(1)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    Row {
        AddToShoppingListDialog(
            showDialog = showDialog,
            onSetShowDialog = { showDialog = it },
            retailers = retailers,
            productPair = productPair,
            coroutineScope = coroutineScope,
            snackbarHostState = snackbarHostState,
            onAddToShoppingList = onAddToShoppingList,
            quantity = quantity,
            region = region
        )

        QuantitySelector(quantity = quantity, setQuantity = { quantity = it }, loading = loading)

        Spacer(modifier = Modifier.weight(1.0f))

        ExtendedFloatingActionButton(
            onClick = {
                showDialog = true
            },
            icon = {
                Icon(
                    Icons.Rounded.ShoppingCart,
                    stringResource(id = R.string.content_description_shopping_cart)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.add)
                )
            },
            modifier = Modifier.placeholder(
                visible = loading,
                shape = FloatingActionButtonDefaults.extendedFabShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                highlight = PlaceholderHighlight.fade()
            )
        )
    }
}

@Composable
private fun AddToShoppingListDialog(
    showDialog: Boolean,
    onSetShowDialog: (Boolean) -> Unit,
    retailers: Map<String, Retailer>?,
    productPair: Pair<String, Product>?,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onAddToShoppingList: ((String, String, String, Int) -> Unit)?,
    quantity: Int?,
    region: String
) {
    var selectedPricingPair by remember {
        mutableStateOf<Pair<String, String>?>(null)
    }

    if (showDialog && retailers != null && productPair != null) {
        val product = productPair.second

        val confirmMessage = stringResource(id = R.string.product_added_to_list)

        AlertDialog(
            onDismissRequest = {
                onSetShowDialog(false)
            },
            title = { Text(stringResource(R.string.add_product_to_shopping_list)) },
            icon = { Icon(Icons.Rounded.ShoppingCart, null) },
            confirmButton = {
                TextButton(onClick = {
                    onSetShowDialog(false)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(confirmMessage)
                    }
                    onAddToShoppingList?.let {
                        it(
                            productPair.first,
                            selectedPricingPair!!.first,
                            selectedPricingPair!!.second,
                            quantity ?: 1
                        )
                    }
                }) {
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onSetShowDialog(false)
                }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                Column {
                    Text(stringResource(R.string.select_shop))

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    var lowestPrice = Int.MAX_VALUE
                    var lowestPriceIsLocal = false

                    val sortedList =
                        product.getFilteredInformation(region, retailers).flatMap { info ->
                            info.pricing!!.map { info to it }
                        }.sortedBy {
                            it.second.getPrice(it.first)
                        }

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        //noinspection RememberReturnType
                        remember {
                            sortedList.forEach { (info, pricing) ->

                                val retailer = retailers[info.retailer]!!

                                val store =
                                    retailer.stores!!.firstOrNull { it.id == pricing.store }

                                if (store != null) {
                                    val price = pricing.getPrice(info)
                                    val pricingPair = Pair(info.id!!, pricing.store!!)

                                    if (retailer.local == true && !lowestPriceIsLocal || (price < lowestPrice && (!lowestPriceIsLocal || retailer.local == true))) {
                                        lowestPrice = price
                                        selectedPricingPair = pricingPair
                                        lowestPriceIsLocal = retailer.local == true
                                    }
                                }
                            }
                        }

                        sortedList.forEach { (info, pricing) ->

                            val store =
                                retailers[info.retailer]!!.stores!!.firstOrNull { it.id == pricing.store }

                            if (store != null) {
                                val pricingPair = Pair(info.id!!, pricing.store!!)

                                if (selectedPricingPair?.first == pricingPair.first && selectedPricingPair?.second == pricingPair.second) {
                                    selectedPricingPair = pricingPair
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = pricingPair == selectedPricingPair,
                                            onClick = {
                                                selectedPricingPair = pricingPair
                                            }
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = pricingPair == selectedPricingPair,
                                        onClick = {
                                            selectedPricingPair = pricingPair
                                        }
                                    )

                                    val retailerName =
                                        retailers[info.retailer]!!.name!!
                                    val storeName = store.name!!

                                    Text(
                                        text = stringResource(
                                            id = R.string.select_store_format,
                                            retailerName,
                                            if (storeName == retailerName) "" else storeName,
                                            (pricing.getPrice(info) * (quantity
                                                ?: 1)).toDouble() / 100f
                                        )
                                    )
                                }

                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.QuantitySelector(
    quantity: Int?,
    setQuantity: (Int?) -> Unit,
    loading: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .align(Alignment.CenterVertically)
    ) {

        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
            val leftShape = RoundedCornerShape(
                topStart = 50f,
                topEnd = 0f,
                bottomStart = 50f,
                bottomEnd = 0f
            )
            FilledTonalIconButton(
                onClick = {
                    setQuantity(
                        quantity?.plus(1) ?: 1
                    )
                },
                modifier = Modifier
                    .placeholder(
                        visible = loading,
                        shape = leftShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
                    .height(50.dp),
                shape = leftShape
            ) {
                Icon(Icons.Rounded.Add, stringResource(id = R.string.increase_quantity))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
                .placeholder(
                    visible = loading,
                    shape = RectangleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    highlight = PlaceholderHighlight.fade()
                )
        ) {
            val cursorColor = MaterialTheme.colorScheme.primary

            BasicTextField(
                value = quantity?.toString() ?: "",
                onValueChange = {
                    val number = it.toIntOrNull()
                    if (number != null && number > 0) {
                        setQuantity(number)
                    } else if (it.isBlank()) {
                        setQuantity(null)
                    }
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(70.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .onFocusChanged {
                        if (!it.isFocused && quantity == null) {
                            setQuantity(1)
                        }
                    },
                cursorBrush = SolidColor(cursorColor)
            )
        }

        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
            val rightShape = RoundedCornerShape(
                topStart = 0f,
                topEnd = 50f,
                bottomStart = 0f,
                bottomEnd = 50f
            )
            FilledTonalIconButton(
                onClick = {
                    if (quantity == null) {
                        setQuantity(1)
                    } else if (quantity > 1) {
                        setQuantity(quantity.minus(1))
                    }
                },
                modifier = Modifier
                    .placeholder(
                        visible = loading,
                        shape = rightShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
                    .height(50.dp),
                shape = rightShape,
                enabled = quantity == null || quantity > 1
            ) {
                Icon(
                    Icons.Rounded.Remove,
                    stringResource(id = R.string.decrease_quantity)
                )
            }
        }
    }
}