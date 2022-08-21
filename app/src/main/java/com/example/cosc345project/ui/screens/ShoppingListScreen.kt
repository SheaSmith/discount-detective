@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        //i.e nothing in shopping list
        if (productIDs != null) {
            //TODO: Ideally should be done in viewmodel
            val grouped = productIDs.groupBy { it.retailerProductInformationID }

            productList(
                grouped = grouped,
                viewModel = viewModel,
                navController = navController,
                innerPadding = innerPadding
            )
        } else {
            Text("Empty Shopping List")
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = innerPadding,
        ){
        grouped.forEach { (store, productsForStore) ->
            stickyHeader {
                Text(store)
            }
            items(productsForStore) { product ->
                ProductCard(
                    product = product,
                    viewModel = viewModel,
                    navController = navController)
            }
        }
    }
}

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

    //Returns a list of all the retailers for that product
    val productRetailerInfoList = productID.let { viewModel.getProductFromID(it).collectAsState(initial = null)}

    //need to get the name and the image (so just first index should be enough)
    //get the retailerID to index into this list (as we have product ID, now need RetailerID)
    val productInfo = productRetailerInfoList.value?.information?.firstOrNull {
        it.retailer == retailerID
    }

    //get product with retailerID, storeID
    //this seems to have broken it. I want to do it this way but maybe check nulls?
    //yes, need to add null checks
    val pricingInfo = productInfo?.pricing?.firstOrNull {it.store == storeId}



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            disabledContainerColor = Color.Transparent
        ),
        shape = CardDefaults.elevatedShape
    ) {
        Column(
            modifier = Modifier.padding(8.dp)) {

            // image loading
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (productInfo != null) {
                    //what placeholder for null image
                    if (productInfo.image != null){
                        AsyncImage(
                            model = productInfo.image,
                            contentDescription = stringResource(id = R.string.content_description_product_image),
                            modifier = Modifier
                                .fillMaxHeight()
                                .height(100.dp)
                                .width(100.dp)
                                .align(Alignment.Top)
                                .placeholder(
                                    visible = false,
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    highlight = PlaceholderHighlight.fade()
                                )
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                        )
                    }
                }
                //text product name
                Column(
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    if (productInfo?.brandName != null) {
                        Text(
                            text = productInfo.brandName ?: "",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    if (productInfo?.name != null) {
                        Text(
                            text = productInfo.name ?: stringResource(id = R.string.placeholder),
                            modifier = Modifier
                                .placeholder(
                                    visible = false,
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    highlight = PlaceholderHighlight.fade()
                                ),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge

                        )
                    }
                    //pricing info
                    Row(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth(),
                    ) {
                        //retailer, product and store for pricing
                        if (pricingInfo?.price != null) {
                            Text(
                                text = pricingInfo.price.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .placeholder(
                                        visible = true,
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        highlight = PlaceholderHighlight.fade()
                                    )
                                    .padding(vertical = 12.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                    //is selected checkbox
                    Row {
                        val isChecked = remember {mutableStateOf(false)}
                        Checkbox(
                            checked = isChecked.value,
                            onCheckedChange = {
                                isChecked.value = it
                            }
                        )
                    }
                }
            }
        }
    }
}


