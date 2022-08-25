@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345project.R
import com.example.cosc345project.models.RetailerProductInfo
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.viewmodel.ShoppingListViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


@Composable
fun getDisplayName(
    viewModel: ShoppingListViewModel,
    retailers: Map<String, Retailer>,
    productID: String,
    retailerProductInfoID: String,
    storeProductInfoID: String
): String {
    //idea if we have product ID then we can get the names
    val productRetailerInfoList = viewModel.getProductFromID(productID).asLiveData()

    //use the retailerID to index into this list (as we have product ID, now need RetailerID)
    val productInfo = productRetailerInfoList.value?.information?.firstOrNull {
        it.id == retailerProductInfoID
    }

    val namePair = viewModel.getStoreName(
        retailerProductInfoID,
        storeProductInfoID,
        retailers,
        productInfo
    )

    //will need this for value bakery
    val displayName = buildString {
        if (namePair.first != null)
            append(namePair.first + ": ")
        append(namePair.second ?: "")

    }
    return displayName
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val retailers by viewModel.retailers.collectAsState()
    //productIDs in the shopping list
    //TODO: No duplicates possible as have same productID
    val productIDs = viewModel.allProducts.observeAsState().value

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val focusManager = LocalFocusManager.current

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // https://stackoverflow.com/a/69140269/7692451
                focusManager.clearFocus()
                return super.onPreScroll(available, source)
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            StatusBarCenterAlignedTopAppBar(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(bottom = 8.dp),
                title = {
                    Text(
                        "Your Shopping List",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (!productIDs.isNullOrEmpty()) {
            val grouped = productIDs.groupBy {
                getDisplayName(
                    viewModel = viewModel,
                    retailers = retailers,
                    productID = it.productID,
                    retailerProductInfoID = it.retailerProductInformationID,
                    storeProductInfoID = it.storePricingInformationID
                )
            }.mapValues { it.value }


            productList(
                grouped = grouped,
                viewModel = viewModel,
                innerPadding = innerPadding,
            )
        } else {
            Text(
                text = "Empty Shopping List",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun productList(
    grouped: Map<String, List<RetailerProductInfo>>,
    viewModel: ShoppingListViewModel,
    innerPadding: PaddingValues,
) {
    val data = remember { mutableStateOf(grouped.values.toList()) }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.value = data.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    })

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = innerPadding,
        state = state.listState,
        modifier = Modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        grouped.forEach { (store, productsForStore) ->
            stickyHeader {
                Text(
                    text = store,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(200.dp)
                        .background(Color.White)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            items(productsForStore) { product ->
                ReorderableItem(
                    reorderableState = state,
                    key = product
                )
                { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                    ProductCard(
                        product = product,
                        viewModel = viewModel,
                        elevation = elevation
                    )
                }
            }
        }
    }
}


@Composable
fun ProductCard(
    product: RetailerProductInfo,
    viewModel: ShoppingListViewModel,
    elevation: State<Dp>
) {
    val productID = product.productID
    //use to index product array
    val retailerID = product.retailerProductInformationID
    val storeId = product.storePricingInformationID


    //collect using 'by' since lazy loading
    val productRetailerInfoList by viewModel.getProductFromID(productID)
        .collectAsState(initial = null)

    //use the retailerID to index into this list (as we have product ID, now need RetailerID)
    val productInfo = productRetailerInfoList?.information?.firstOrNull {
        it.id == retailerID
    }

    //get product with retailerID, storeID
    val pricingInfo = productInfo?.pricing?.firstOrNull { it.store == storeId }

    //checkbox Checked
    val isChecked = remember { mutableStateOf(false) }


    //add this in for the correct colour
    //tonalElevation = 2.dp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(elevation.value),
        colors = CardDefaults.elevatedCardColors(
            disabledContainerColor = Color.Transparent
        ),
        shape = CardDefaults.elevatedShape,
        elevation = if (productInfo != null) CardDefaults.cardElevation() else CardDefaults.elevatedCardElevation()
    ) {
        // master row layout
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxHeight()
                .safeContentPadding(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.DragHandle,
                contentDescription ="dd",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            AsyncImage(
                model = productInfo?.image,
                contentDescription = stringResource(id = R.string.content_description_product_image),
                modifier = Modifier
                    .alpha(if (isChecked.value) 1.0f else 0.5f)
                    .fillMaxHeight()
                    .height(60.dp)
                    .width(60.dp)
                    .placeholder(
                        visible = productInfo == null,
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                )

            //brand-name, product name and pricing info
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ){
                Text(
                    // add the variant, quantity to title
                    // productInfo?.name ?: stringResource(id = R.string.placeholder)
                    text = buildAnnotatedString {
                        append(productInfo?.name ?: stringResource(id = R.string.placeholder))
                        append("; Quantity: ")
                        append(product.quantity.toString())
                        toAnnotatedString()
                    },
                    modifier = Modifier
                        .placeholder(
                            visible = productInfo == null,
                            shape = RoundedCornerShape(1.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            highlight = PlaceholderHighlight.fade()
                        ),
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleSmall

                )
                val saleType = if (productInfo?.saleType == SaleType.WEIGHT) {
                    "kg"
                } else {
                    "/ea"
                }
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 38.sp
                            )
                        ) {
                            append("$")
                            append(
                                ((pricingInfo?.getPrice(productInfo)?.toDouble()
                                    ?: 100.0) / 100).toString()
                            )
                        }
                        append(" $saleType")
                    },
                    modifier = Modifier
                        .placeholder(
                            visible = productInfo == null,
                            shape = RoundedCornerShape(1.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            highlight = PlaceholderHighlight.fade()
                        ),
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Column {
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = {
                        isChecked.value = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
