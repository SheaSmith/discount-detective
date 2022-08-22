@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.R
import com.example.cosc345project.models.RetailerProductInfo
import com.example.cosc345project.ui.components.MinimumHeightState
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.ui.components.minimumHeightModifier
import com.example.cosc345project.viewmodel.ShoppingListViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    //productIDs in the shopping list
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
        //i.e nothing in shopping list
        if (!productIDs.isNullOrEmpty()) {
            //TODO: Ideally should be done in viewmodel
            // RetailerRepository has this info
            val grouped = productIDs.groupBy { it.storePricingInformationID }

            productList(
                grouped = grouped,
                viewModel = viewModel,
                navController = navController,
                innerPadding = innerPadding
            )
        } else {
            Text(
                text = "Empty Shopping List",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun productList(grouped: Map<String, List<RetailerProductInfo>>,
                viewModel: ShoppingListViewModel,
                navController: NavHostController,
                innerPadding: PaddingValues
) {
    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = innerPadding,
        ){
        grouped.forEach { (store, productsForStore) ->
            stickyHeader {
                Text(
                    text = store,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(200.dp)
                )
            }
            //this not working (nothing but text box displayed)
            items(productsForStore) { product ->
                ProductCard(
                    product = product,
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }
    }
}

/**
 * Class for the Shopping List Screen.
 *
 * Creates a shopping list page so the user can see and interact with the items they
 * have added to their shopping list.
 *
 */
@Composable
fun ProductCard(
    product: RetailerProductInfo,
    viewModel: ShoppingListViewModel,
    navController: NavHostController
) {
    val productID = product.productID
    //use to index product array
    val retailerID = product.retailerProductInformationID
    val storeId = product.storePricingInformationID


    //collect using by since lazy loading
    val productRetailerInfoList by viewModel.getProductFromID(productID).collectAsState(initial = null)

    //use the retailerID to index into this list (as we have product ID, now need RetailerID)
    val productInfo = productRetailerInfoList?.information?.firstOrNull {
        it.id == retailerID
    }

    //get product with retailerID, storeID
    //this seems to have broken it. I want to do it this way but maybe check nulls?
    val pricingInfo = productInfo?.pricing?.firstOrNull {it.store == storeId}

    //use getDisplayPrice (not currently available)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            disabledContainerColor = Color.Transparent
        ),
        shape = CardDefaults.elevatedShape
    ) {
        // master row layout
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
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
                    .fillMaxHeight()
                    .height(30.dp)
                    .width(30.dp)
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
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = productInfo?.name ?: stringResource(id = R.string.placeholder),
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
                Text(
                    //TODO: getPrice function
                    // then use search product
                    text = pricingInfo?.price.toString(),
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
                val isChecked = remember { mutableStateOf(false) }
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


