@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.R
import com.example.cosc345project.models.RetailerProductInfo
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.viewmodel.ShoppingListViewModel

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
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                        shape = CircleShape,
                        tonalElevation = 16.dp
                    ) {
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
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = innerPadding,
            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ){
            if (productIDs.isNullOrEmpty()){
                items(10){
                    ProductCard(
                        product = null,
                        loading = false,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
            if (productIDs != null) {
                items(productIDs.size) { index ->
                    ProductCard(
                        product = productIDs[index],
                        loading = false,
                        viewModel = viewModel,
                        navController = navController)
                }
            }
        }

    }
}

@Composable
fun ProductCard(
    product: RetailerProductInfo?,
    loading: Boolean,
    viewModel: ShoppingListViewModel,
    navController: NavHostController
) {
    val productID = product?.productID
    //use to index product array
    val retailerID = product?.retailerProductInformationID

    //Returns a list of all the retailers for that product
    val productRetailerInfoList = productID?.let { viewModel.getProductFromID(it).collectAsState(initial = null)}

    //need to get the name and the image (so just first index should be enough)
    //get the retailerID to index into this list (as we have product ID, now need RetailerID)
    val productInformation = productRetailerInfoList?.value?.information?.first().let{item ->
        if (item?.name == retailerID) item else RetailerProductInformation()
    }

    //information to populate the card entries
    val productName = productInformation?.name
    val productImage = productInformation?.image
    val productPrice = productInformation?.pricing
    val retailer = productInformation?.retailer

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            disabledContainerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            if (productName != null) {
                Text(
                    text = productName
                )
            } else {
                Text(
                    text = "placeholder"
                )
            }
        }
    }
}


