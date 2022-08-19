@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.example.cosc345.shared.models.StorePricingInformation
import com.example.cosc345project.R
import com.example.cosc345project.models.RetailerProductInfo
import com.example.cosc345project.ui.components.MinimumHeightState
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.ui.components.minimumHeightModifier
import com.example.cosc345project.ui.components.search.SearchProductPricingBlock
import com.example.cosc345project.viewmodel.ShoppingListViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
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
        topBar = { //change this to johns product screen
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
                items(0){
                    ProductCard(
                        product = null,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
            if (productIDs != null) {
                items(productIDs.size) { index ->
                    ProductCard(
                        product = productIDs[index],
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
    viewModel: ShoppingListViewModel,
    navController: NavHostController
) {
    val productID = product?.productID
    //use to index product array
    val retailerID = product?.retailerProductInformationID
    val storeId = product?.storePricingInformationID

    //Returns a list of all the retailers for that product
    val productRetailerInfoList = productID?.let { viewModel.getProductFromID(it).collectAsState(initial = null)}

    //need to get the name and the image (so just first index should be enough)
    //get the retailerID to index into this list (as we have product ID, now need RetailerID)
    val productInfo = productRetailerInfoList?.value?.information?.first().let{item ->
        if (item?.name == retailerID) item else RetailerProductInformation()
    }

    //get product with retailerID, storeID
    val pricingInfo = productInfo?.pricing?.first().let{item ->
        if (item?.store == storeId) item else StorePricingInformation()
    }



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
                if (productInfo?.image != null){
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

                    Text(
                        text = productInfo?.name ?: stringResource(id = R.string.placeholder),
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

                        //retailer, product and store for pricing
                        Text(
                            text = pricingInfo?.price.toString() ?: stringResource(id = R.string.placeholder),
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
            }
        }
    }
}


