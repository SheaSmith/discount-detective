@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345project.R
import com.example.cosc345project.extensions.getBestInformation
import com.example.cosc345project.extensions.getPrice
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.ui.components.MinimumHeightState
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.ui.components.minimumHeightModifier
import com.example.cosc345project.viewmodel.SearchViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Class for the Search Screen
 *
 * Creates the user interface search screen and links to the database so that users can search
 * for certain foods.
 *
 * @param viewModel
 * @param navController
 */
@Composable
@Preview
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val search by viewModel.searchQuery.collectAsState()
    val retailers by viewModel.retailers.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val searchResults by viewModel.searchLiveData
    val productResults = searchResults.collectAsLazyPagingItems()
    val loading by remember {
        viewModel.loading
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val focusManager = LocalFocusManager.current
    var showSuggestions by remember {
        viewModel.showSuggestions
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            StatusBarCenterAlignedTopAppBar(
                modifier = Modifier
                    .statusBarsPadding(),
                title = {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                        shape = CircleShape,
                        tonalElevation = 16.dp
                    ) {
                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = stringResource(id = R.string.search),
                                    modifier = Modifier.padding(start = 16.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                TextField(
                                    value = search,
                                    placeholder = { Text(stringResource(id = R.string.search_products)) },
                                    onValueChange = {
                                        viewModel.setQuery(it)
                                        coroutineScope.launch {
                                            listState.scrollToItem(0)
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(0.dp)
                                        .onFocusChanged {
                                            showSuggestions = it.isFocused
                                        },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            viewModel.query()
                                        }
                                    ),
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        containerColor = Color.Transparent
                                    )
                                )
                                AnimatedVisibility(visible = search.isNotEmpty()) {
                                    IconButton(onClick = {
                                        viewModel.setQuery("", runSearch = true)
                                    }) {
                                        Icon(
                                            Icons.Rounded.Close,
                                            stringResource(id = R.string.content_description_clear_search)
                                        )
                                    }
                                }
                            }

                            if (loading) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                innerPadding.calculateTopPadding() + 4.dp,
                innerPadding.calculateEndPadding(
                    LocalLayoutDirection.current
                ),
                innerPadding.calculateBottomPadding() + 8.dp
            ),
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { focusManager.clearFocus() }
                    )
                },
            state = listState
        ) {
            if (showSuggestions && suggestions.isNotEmpty()) {
                items(
                    items = suggestions
                ) {
                    ListItem(
                        headlineText = { Text(text = it) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable {
                                viewModel.setQuery(it, true)
                                focusManager.clearFocus()
                            }
                    )
                }
            } else {
                if (productResults.itemCount == 0 || retailers.isEmpty()) {
                    items(10) {
                        ProductCard(
                            product = null,
                            loading = productResults.itemCount == 0,
                            viewModel,
                            navController,
                            retailers,
                            snackbarHostState,
                            coroutineScope
                        )
                    }
                } else if (retailers.isNotEmpty()) {
                    items(
                        items = productResults,
                        key = { product -> product.id }
                    ) {
                        ProductCard(
                            it,
                            loading,
                            viewModel,
                            navController,
                            retailers,
                            snackbarHostState,
                            coroutineScope
                        )
                    }
                }
            }
        }

    }
}

/**
 * Product card function displays each product's information
 *
 * Creates a section ("product card") for each product in the search screen, so that users can
 * easily differentiate between the information of each product and click on it to open the
 * corresponding product screen.
 *
 * @param product
 * @param loading
 * @param viewModel
 * @param navController
 */
@Composable
fun ProductCard(
    product: SearchableProduct?,
    loading: Boolean,
    viewModel: SearchViewModel,
    navController: NavHostController,
    retailers: Map<String, Retailer>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val info = product?.getBestInformation()
    val localPrice = product?.let { viewModel.getLocalPrice(it) }
    val bestPrice = product?.let { viewModel.getBestPrice(it) }

    Card(
        onClick = {
            if (product != null) {
                navController.navigate("products/${product.id}")
            }
        },
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            disabledContainerColor = Color.Transparent
        ),
        elevation = if (loading) CardDefaults.cardElevation() else CardDefaults.elevatedCardElevation(),
        shape = CardDefaults.elevatedShape
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

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
                    if (info?.brandName != null) {
                        Text(
                            text = info.brandName,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Text(
                        text = info?.name ?: stringResource(id = R.string.placeholder),
                        modifier = Modifier
                            .placeholder(
                                visible = loading,
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                highlight = PlaceholderHighlight.fade()
                            ),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )

                    if (info?.variant != null || info?.quantity != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (info.variant != null) {
                                Text(
                                    text = info.variant,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            if (info.quantity != null) {
                                Text(
                                    text = info.quantity,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

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
                            minimumHeightState,
                            density
                        )

                        PricingBlock(
                            components = bestPrice,
                            loading = loading,
                            local = false,
                            modifier = minimumHeightStateModifier
                        )

                        PricingBlock(
                            components = localPrice,
                            loading = loading,
                            local = true,
                            modifier = minimumHeightStateModifier
                        )
                    }

                }
            }

            Row {
                var quantity by remember {
                    mutableStateOf<Int?>(1)
                }

                var selectedPricingPair by remember {
                    mutableStateOf<Pair<String, String>?>(null)
                }

                var showDialog by remember {
                    mutableStateOf(false)
                }

                if (showDialog) {
                    val confirmMessage = stringResource(id = R.string.product_added_to_list)

                    AlertDialog(
                        onDismissRequest = {
                            showDialog = false
                        },
                        title = { Text(stringResource(R.string.add_product_to_shopping_list)) },
                        icon = { Icon(Icons.Rounded.ShoppingCart, null) },
                        confirmButton = {
                            Button(onClick = {
                                showDialog = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(confirmMessage)
                                }
                            }) {
                                Text(stringResource(R.string.add))
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
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
                                    info.pricing.minByOrNull {
                                        it.getPrice(
                                            info
                                        )
                                    }!!
                                        .getPrice(info)
                                }

                                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                    sortedList.forEach { info ->
                                        val sortedPricing =
                                            info.pricing.sortedBy { it.getPrice(info) }
                                        sortedPricing.forEach { pricing ->
                                            val store =
                                                retailers[pricing.retailer]!!.stores!!.firstOrNull { it.id == pricing.store }

                                            if (store != null) {
                                                val pair = Pair(info.id, pricing.store)

                                                val price = pricing.getPrice(info)

                                                if (info.local && !lowestPriceIsLocal || (price < lowestPrice && (!lowestPriceIsLocal || info.local))) {
                                                    lowestPrice = price
                                                    selectedPricingPair = pair
                                                    lowestPriceIsLocal = info.local
                                                }
                                            }
                                        }
                                    }

                                    sortedList.forEach { info ->
                                        val sortedPricing =
                                            info.pricing.sortedBy { it.getPrice(info) }

                                        sortedPricing.forEach { pricing ->
                                            val store =
                                                retailers[pricing.retailer]!!.stores!!.firstOrNull { it.id == pricing.store }

                                            if (store != null) {
                                                val pair = Pair(info.id, pricing.store)

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
                                                        retailers[pricing.retailer]!!.name!!
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
    }
}

/**
 * PricingBlock: finds and displays the pricing information for each product.
 *
 * Displays both the cheapest price and the cheapest local price so that users have the option of
 * choosing either.
 *
 * @param components
 * @param loading
 * @param local
 */
@Composable
fun PricingBlock(
    components: Pair<String, String>?,
    loading: Boolean,
    local: Boolean,
    modifier: Modifier = Modifier
) {
    if (components != null || loading) {
        Column(
            modifier = modifier.width(IntrinsicSize.Min),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = if (local) R.string.best_local_price else R.string.best_price),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = if (loading) 4.dp else 0.dp)
                    .fillMaxWidth()
                    .placeholder(
                        visible = loading,
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 38.sp
                        )
                    ) {
                        append(components?.first ?: "$10")
                    }
                    append(components?.second ?: ".00/kg")
                },
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .placeholder(
                        visible = loading,
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
                    .width(IntrinsicSize.Max)
            )
        }
    }
}