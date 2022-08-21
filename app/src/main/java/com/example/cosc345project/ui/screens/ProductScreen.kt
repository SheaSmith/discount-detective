@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import android.icu.text.NumberFormat
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.cosc345project.R
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.settings.indexSettingsDataStore
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.viewmodel.SearchViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import kotlinx.coroutines.flow.map
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.cosc345project.ui.theme.DiscountDetectiveTheme
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign

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

// this is just for testing, should probably change it from using Product //
data class Product(val name : String, val retailerName: String, val productPrice : Number, val productSalePrice : Number, val unit : String)
var exampleProduct = Product("Rump Steak", "Leckies Bakery", 40000, 40000, "kg")
var exampleProducts = listOf(exampleProduct)

@Composable
fun LargeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable @ExtensionFunctionType RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
): Unit {
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ProductImage(padding: PaddingValues) {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
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

        Box(
            Modifier
                .align(Alignment.TopCenter)
                .height(250.dp)
                .offset(x = 0.dp, y = 450.dp)
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth()


        ) {
            RetailerPrices(padding, exampleProducts)
        }


    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun RetailerSlot(padding: PaddingValues, product: Product) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),

        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp
    ) {
        Row () {

            Surface(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .padding(padding),

                shape = CircleShape,

                tonalElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LB",
                        fontSize = 24.sp
                    )

                }

            }

            Spacer(Modifier.width(10.0.dp))

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Leckies Butchery",
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(15.0.dp))

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$" + product.productPrice.toString(),
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(50.0.dp))

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$" + product.productPrice.toString(),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun RetailerPrices(padding: PaddingValues, products: List<Product>) {
    Scaffold(


        content = { padding ->
            Box(
                modifier = Modifier
                    .offset(x = 0.dp, y = 0.dp)
            ) {
                Column () {
                    Row() {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { /* ... */ },
                                // Uses ButtonDefaults.ContentPadding by default
                                contentPadding = PaddingValues(
                                    start = 20.dp,
                                    top = 12.dp,
                                    end = 20.dp,
                                    bottom = 12.dp
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(50.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+1",
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { /* ... */ },
                                // Uses ButtonDefaults.ContentPadding by default
                                contentPadding = PaddingValues(
                                    start = 20.dp,
                                    top = 12.dp,
                                    end = 20.dp,
                                    bottom = 12.dp
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(50.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "1",
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { /* ... */ },
                                // Uses ButtonDefaults.ContentPadding by default
                                contentPadding = PaddingValues(
                                    start = 20.dp,
                                    top = 12.dp,
                                    end = 20.dp,
                                    bottom = 12.dp
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(50.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "-1",
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }
                    }




                    Spacer(Modifier.height(10.0.dp))

                    Row(
                        modifier = Modifier
                            .padding(top = 0.dp)
                            .fillMaxWidth() ,

                        //horizontalArrangement = Arrangement.spacedBy(100.dp)

                    ) {

                        Text(text = "Retailer", fontSize = 18.sp)
                        Spacer(Modifier.width(110.0.dp))
                        Text(text = "Price", fontSize = 18.sp)
                        Spacer(Modifier.width(60.0.dp))
                        Text(text = "Sale Price", fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(-10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp, end = 0.dp, start = 0.dp)
                    ) {
                        LazyColumn() {
                            items(products) { product ->
                                RetailerSlot(padding, product)
                            }
                        }


                    }
                }




            }

        }
    )


}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
@Preview
fun ProductScreen() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Could Be This Many Characters Or Even More",
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

                scrollBehavior = scrollBehavior
            )
        },

        content = { padding ->
            ProductImage(padding)
        }
    )

}