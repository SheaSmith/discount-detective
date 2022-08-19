@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosc345project.R
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation
import com.example.cosc345project.ui.components.StatusBarLargeTopAppBar
import com.example.cosc345project.ui.components.product.AddToShoppingListBlock
import com.example.cosc345project.ui.components.product.ProductTitle
import com.example.cosc345project.viewmodel.ProductViewModel

/*
* 19/08 - John
* Need to check if scrolling actually works
* Need to make title change
* Need to position +1 -1 buttons
* Need to add "Add Item" button
* I removed the title that goes below image because we have it at the top anyway
* and if we have long named items then it will get very messy
* Need to make image change to product image
* Will probably need to check with Shea on getting the product info
* Need to comment... yucky
* */

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ProductImage() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(250.dp)
            .offset(x = 0.dp, y = 175.dp)


    ) {
        Image(
            modifier = Modifier
                .padding(0.dp)
                .align(Alignment.Center)
                .aspectRatio(1.5f)
                .fillMaxHeight()
                .background(Color.White),


            painter = painterResource(R.drawable.banana_single),
            contentDescription = "background_image",
            contentScale = ContentScale.FillHeight,

            )
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun RetailerSlot(
    pricingInformation: StorePricingInformation,
    retailer: Retailer,
    productInformation: RetailerProductInformation
) {
    val store = retailer.stores!!.first { it.id == pricingInformation.store }

    Surface(
        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp
    ) {
        Row {

            Surface(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .align(Alignment.CenterVertically),
                shape = CircleShape,
                color = Color(if (isSystemInDarkTheme()) retailer.colourDark!! else retailer.colourLight!!)
            ) {
                Text(
                    text = retailer.initialism!!,
                    fontSize = 24.sp,
                    color = Color(if (isSystemInDarkTheme()) retailer.onColourDark!! else retailer.onColourLight!!)
                )

            }

            Spacer(Modifier.width(10.0.dp))

            Column(
                modifier = Modifier
                    .weight(1.0f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = store.name!!,
                    fontSize = 16.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
            ) {
                val price = pricingInformation.price?.let {
                    pricingInformation.getDisplayPrice(
                        productInformation,
                        it
                    )
                }

                Text(
                    text = price?.let { "$${price.first}${price.second}" } ?: "",
                    fontSize = 16.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
            ) {
                if (pricingInformation.discountPrice != null) {
                    val price = pricingInformation.getDisplayPrice(
                        productInformation,
                        pricingInformation.discountPrice!!
                    )

                    Text(
                        text = "$${price.first}${price.second}",
                        fontSize = 16.sp
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
                        text = stringResource(id = R.string.club_only),
                        fontSize = 16.sp
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

    Column {
        ProductTitle(info = bestInformation, loading = false)

        AddToShoppingListBlock(
            snackbarHostState = snackbarHostState,
            productPair = product,
            retailers = retailers,
            loading = loading
        )
    }


}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
@Preview
fun ProductScreen(viewModel: ProductViewModel = hiltViewModel()) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

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
                        product.second.getBestInformation().name ?: "",
                        maxLines = 2,
                        //style = MaterialTheme.typography.displaySmall,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .offset(x = 0.dp, y = 0.dp),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
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
                    ProductImage()
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
                        Text(text = stringResource(id = R.string.local_prices))

                        TableHeader()
                    }

                    items(localRetailerProductInformation.flatMap { info -> info.pricing!!.map { it to info } }) { item ->
                        RetailerSlot(
                            pricingInformation = item.first,
                            retailer = retailers[item.second.retailer]!!,
                            productInformation = item.second
                        )
                    }
                }

                if (nonLocalRetailerProductInformation.isNotEmpty() && retailers.isNotEmpty()) {
                    item {
                        Text(text = stringResource(id = R.string.local_prices))

                        TableHeader()
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
fun TableHeader() {
    Row {
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
            modifier = Modifier.weight(0.5f)
        )
    }
}