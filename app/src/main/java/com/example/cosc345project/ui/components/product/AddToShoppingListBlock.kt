package com.example.cosc345project.ui.components.product

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
import com.example.cosc345project.R
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToShoppingListBlock(
    snackbarHostState: SnackbarHostState,
    productPair: Pair<String, Product>?,
    retailers: Map<String, Retailer>,
    loading: Boolean,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val product = productPair?.second

    var quantity by remember {
        mutableStateOf<Int?>(1)
    }

    var selectedPricingPair by remember {
        mutableStateOf<Pair<String, String>?>(null)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    Row {
        if (showDialog) {
            val confirmMessage = stringResource(id = R.string.product_added_to_list)

            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = { Text(stringResource(R.string.add_product_to_shopping_list)) },
                icon = { Icon(Icons.Rounded.ShoppingCart, null) },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(confirmMessage)
                        }
                    }) {
                        Text(stringResource(R.string.add))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
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

                        val sortedList = product!!.information!!.sortedBy { info ->
                            info.pricing!!.minByOrNull {
                                it.getPrice(
                                    info
                                )
                            }!!
                                .getPrice(info)
                        }

                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            sortedList.forEach { info ->
                                val sortedPricing =
                                    info.pricing!!.sortedBy { it.getPrice(info) }
                                sortedPricing.forEach { pricing ->
                                    val retailer = retailers[info.retailer]!!

                                    val store =
                                        retailer.stores!!.firstOrNull { it.id == pricing.store }

                                    if (store != null) {
                                        val pair = Pair(info.id!!, pricing.store!!)

                                        val price = pricing.getPrice(info)

                                        if (retailer.local == true && !lowestPriceIsLocal || (price < lowestPrice && (!lowestPriceIsLocal || retailer.local == true))) {
                                            lowestPrice = price
                                            selectedPricingPair = pair
                                            lowestPriceIsLocal = retailer.local == true
                                        }
                                    }
                                }
                            }

                            sortedList.forEach { info ->
                                val sortedPricing =
                                    info.pricing!!.sortedBy { it.getPrice(info) }

                                sortedPricing.forEach { pricing ->
                                    val store =
                                        retailers[info.retailer]!!.stores!!.firstOrNull { it.id == pricing.store }

                                    if (store != null) {
                                        val pair = Pair(info.id!!, pricing.store!!)

                                        if (selectedPricingPair?.first == pair.first && selectedPricingPair?.second == pair.second) {
                                            selectedPricingPair = pair
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .selectable(
                                                    selected = pair == selectedPricingPair,
                                                    onClick = {
                                                        selectedPricingPair = pair
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = pair == selectedPricingPair,
                                                onClick = {
                                                    selectedPricingPair = pair
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
                }
            )
        }

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
                        quantity = if (quantity == null) {
                            1
                        } else {
                            quantity!!.plus(1)
                        }
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
                val cursorColorState =
                    TextFieldDefaults.textFieldColors().cursorColor(isError = false)
                val cursorColor by remember {
                    cursorColorState
                }

                BasicTextField(
                    value = quantity?.toString() ?: "",
                    onValueChange = {
                        val number = it.toIntOrNull()
                        if (number != null && number > 0) {
                            quantity = number
                        } else if (it.isBlank()) {
                            quantity = null
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
                                quantity = 1
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
                            quantity = 1
                        } else if (quantity!! > 1) {
                            quantity = quantity!!.minus(1)
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
                    enabled = quantity == null || quantity!! > 1
                ) {
                    Icon(
                        Icons.Rounded.Remove,
                        stringResource(id = R.string.decrease_quantity)
                    )
                }
            }
        }

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