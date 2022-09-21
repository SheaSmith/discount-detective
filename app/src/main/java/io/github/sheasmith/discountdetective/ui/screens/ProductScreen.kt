@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.sheasmith.discountdetective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import io.github.sheasmith.discountdetective.R
import io.github.sheasmith.discountdetective.ui.components.StatusBarLargeTopAppBar
import io.github.sheasmith.discountdetective.ui.components.product.AddToShoppingListBlock
import io.github.sheasmith.discountdetective.ui.components.product.ProductTitle
import io.github.sheasmith.discountdetective.ui.components.product.RetailerSlot
import io.github.sheasmith.discountdetective.ui.components.product.TableHeader
import io.github.sheasmith.discountdetective.viewmodel.ProductViewModel

/**
 * Function used to create the image for the Product on the Product Screen.
 *
 * @param image The image from the Product information to be displayed.
 */
@Composable
fun ProductImage(image: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(bottom = 10.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            contentScale = ContentScale.FillHeight,
        )
    }
}

/**
 * Function used to handle adding the product to the shopping list.
 *
 * @param product The product selected to be added to the shopping list.
 * @param snackbarHostState The passed down SnackbarHostState.
 * @param retailers List of retailers who are selling the product.
 */
@Composable
fun ProductInformation(
    product: Pair<String, Product>?,
    snackbarHostState: SnackbarHostState,
    retailers: Map<String, Retailer>?,
    viewModel: ProductViewModel
) {
    val loading = product == null
    val region by viewModel.region.observeAsState()

    if (region != null) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
        )
        {
            AddToShoppingListBlock(
                snackbarHostState = snackbarHostState,
                productPair = product,
                retailers = retailers,
                loading = loading,
                onAddToShoppingList = { productId, retailerProductInfoId, storeId, quantity ->
                    viewModel.addToShoppingList(productId, retailerProductInfoId, storeId, quantity)
                },
                region = region!!
            )
        }
    }
}

/**
 * Function used to layout the main content of the product screen page.
 *
 * @param productId A string representing the id of the product.
 * @param viewModel Instance of the ProductViewModel class (see [ProductViewModel])
 * @param nav Instance of the nav controller for navigation class.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    productId: String,
    viewModel: ProductViewModel = hiltViewModel(),
    nav: NavHostController
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    viewModel.getProduct(productId)

    val snackbarHostState = remember { SnackbarHostState() }

    val retailers by viewModel.retailers.collectAsState()
    val product by viewModel.product.collectAsState()

    val localRetailerProductInformation by viewModel.localRetailerInfo.collectAsState()
    val nonLocalRetailerProductInformation by viewModel.nonLocalRetailerInfo.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        topBar = {
            StatusBarLargeTopAppBar(
                title = {
                    ProductTitle(
                        info = product?.second?.getBestInformation(),
                        loading = product == null,
                        applyStyling = false
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        nav.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                modifier = Modifier
                    .statusBarsPadding(),
                scrollBehavior = scrollBehavior
            )
        },

        content = { padding ->
            LazyColumn(contentPadding = padding) {
                item {
                    ProductImage(image = product?.second?.getBestInformation()?.image)
                }

                item {
                    ProductInformation(
                        product = product,
                        snackbarHostState = snackbarHostState,
                        retailers = retailers,
                        viewModel = viewModel
                    )
                }

                tableContent(
                    retailers = retailers,
                    localRetailerProductInformation = localRetailerProductInformation,
                    nonLocalRetailerProductInformation = nonLocalRetailerProductInformation
                )
            }
        }
    )

}

private fun LazyListScope.tableContent(
    localRetailerProductInformation: List<RetailerProductInformation>,
    nonLocalRetailerProductInformation: List<RetailerProductInformation>,
    retailers: Map<String, Retailer>
) {


    if (localRetailerProductInformation.isNotEmpty() && retailers.isNotEmpty()) {
        item {
            TableHeader(true)
        }

        //var sortedInformation = localRetailerProductInformation.sortedByDescending { it.pricing }

        val sortedList = localRetailerProductInformation.flatMap { info ->
            info.pricing!!.map { info to it }
        }.sortedBy {
            it.second.getPrice(it.first)
        }

        items(sortedList) { (first, second) ->
            RetailerSlot(
                pricingInformation = second,
                retailer = retailers[first.retailer]!!,
                productInformation = first
            )

        }
    }

    val sortedList = nonLocalRetailerProductInformation.flatMap { info ->
        info.pricing!!.map { info to it }
    }.sortedBy {
        it.second.getPrice(it.first)
    }


    if (nonLocalRetailerProductInformation.isNotEmpty() && retailers.isNotEmpty()) {
        item {
            TableHeader(false)
        }

        items(sortedList) { (first, second) ->
            RetailerSlot(
                pricingInformation = second,
                retailer = retailers[first.retailer]!!,
                productInformation = first
            )
        }
    }
}