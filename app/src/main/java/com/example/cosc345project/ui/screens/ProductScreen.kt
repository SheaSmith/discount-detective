@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation
import com.example.cosc345project.R
import com.example.cosc345project.ui.components.StatusBarLargeTopAppBar
import com.example.cosc345project.ui.components.product.AddToShoppingListBlock
import com.example.cosc345project.ui.components.product.ProductTitle
import com.example.cosc345project.viewmodel.ProductViewModel


@Composable
fun ProductImage(image: String?) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)) {
        AsyncImage(
            model = image,
            contentDescription = "Image of death",
            modifier = Modifier
                .padding(0.dp)
                .aspectRatio(1.5f)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White),
            contentScale = ContentScale.FillHeight,
        )
    }
}

@Composable
fun RetailerSlot(
    pricingInformation: StorePricingInformation,
    retailer: Retailer,
    productInformation: RetailerProductInformation
) {
    val store = retailer.stores!!.first { it.id == pricingInformation.store }

    Surface(
        modifier = Modifier
            .height(70.dp)
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .fillMaxWidth(),

        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp
    ) {
        Row {

            Surface(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .width(45.dp)
                    .height(45.dp)
                    .align(Alignment.CenterVertically),
                shape = CircleShape,
                border = BorderStroke(
                    width = 5.dp,
                    color = Color.White.copy(alpha = 0.6f),


                    ),
                color = Color(if (isSystemInDarkTheme()) retailer.colourDark!! else retailer.colourLight!!)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = retailer.initialism!!,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(if (isSystemInDarkTheme()) retailer.onColourDark!! else retailer.onColourLight!!)
                    )
                }


            }

            Spacer(Modifier.width(10.0.dp))

            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight()
                        .padding(end = 10.dp),
                    //.width(intrinsicSize = IntrinsicSize.Min)

                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = store.name!!,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        maxLines = 3,

                    )
                }

            }

            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .align(Alignment.CenterVertically)
            ) {
                val price = pricingInformation.price?.let {
                    pricingInformation.getDisplayPrice(
                        productInformation,
                        it
                    )
                }

                Text(
                    text = price?.let { "${price.first}${price.second}" } ?: "",
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .align(Alignment.CenterVertically)
            ) {
                if (pricingInformation.discountPrice != null) {
                    val price = pricingInformation.getDisplayPrice(
                        productInformation,
                        pricingInformation.discountPrice!!
                    )

                    Text(
                        text = "${price.first}${price.second}",
                        fontSize = 14.sp
                    )
                }

                if (pricingInformation.multiBuyPrice != null && pricingInformation.multiBuyQuantity != null) {
                    val price = pricingInformation.getDisplayPrice(
                        productInformation,
                        pricingInformation.discountPrice!!
                    )

                    Text(
                        text = "${pricingInformation.multiBuyQuantity} for $${price.first}${price.second}",
                        fontSize = 16.sp
                    )
                }

                if (pricingInformation.clubOnly == true) {
                    Text(
                        text = stringResource(
                            id = R.string.club_only,
                        ),

                        lineHeight = 16.sp,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ProductInformation(
    product: Pair<String, Product>?,
    snackbarHostState: SnackbarHostState,
    retailers: Map<String, Retailer>?
) {
    val bestInformation = product?.second?.getBestInformation()
    val loading = false

    Column (
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp)
    )
    {
        ProductTitle(info = bestInformation, loading = false)

        AddToShoppingListBlock(
            snackbarHostState = snackbarHostState,
            productPair = product,
            retailers = retailers,
            loading = loading,
            onAddToShoppingList = { id, id2, id3, id4 ->

            }
        )
    }


}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ProductScreen(productId: String, viewModel: ProductViewModel = hiltViewModel(), nav : NavHostController) {
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
                    Text(
                        product?.second?.getBestInformation()?.name ?: "",
                        maxLines = 2,
                        //style = MaterialTheme.typography.displaySmall,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .offset(x = 0.dp, y = 0.dp),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        nav.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
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
                        retailers = retailers
                    )
                }

                if (localRetailerProductInformation.isNotEmpty() && retailers.isNotEmpty()) {
                    item {
                        TableHeader(true)
                    }

                    items(localRetailerProductInformation.flatMap { info -> info.pricing!!.map { it to info } }) { item ->
                        RetailerSlot(
                            pricingInformation = item.first,
                            retailer = retailers[item.second.retailer]!!,
                            productInformation = item.second
                        )

//
                    }
                }

                if (nonLocalRetailerProductInformation.isNotEmpty() && retailers.isNotEmpty()) {
                    item {
                        TableHeader(false)
                    }

                    items(nonLocalRetailerProductInformation.flatMap { info -> info.pricing!!.map { it to info } }) { item ->
                        RetailerSlot(
                            pricingInformation = item.first,
                            retailer = retailers[item.second.retailer]!!,
                            productInformation = item.second
                        )
                    }
                }

            }
        }
    )

}

@Composable
fun TableHeader(local : Boolean) {
    if (local) {
        Text(
            text = stringResource(id = R.string.local_prices),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 10.dp)

        )
    }

    Row (
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    )
    {
        Text(
            text = stringResource(id = R.string.retailer),
            modifier = Modifier.weight(1.0f)
        )

        Text(
            text = stringResource(id = R.string.price),
            modifier = Modifier.weight(0.5f)
        )

        Text(
            text = stringResource(id = R.string.discount_price),
            modifier = Modifier.weight(0.5f),
            lineHeight = 18.sp
        )
    }
}