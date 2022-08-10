@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.input.VisualTransformation
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
import com.example.cosc345project.R
import com.example.cosc345project.extensions.getBestInformation
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.viewmodel.SearchViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
@Preview
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    var search by viewModel.searchQuery
    val searchResults by viewModel.searchLiveData
    val productResults = searchResults.collectAsLazyPagingItems()
    val loading by remember {
        viewModel.loading
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val focusManager = LocalFocusManager.current

    // https://stackoverflow.com/a/70460377/7692451
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
                            TextField(
                                value = search,
                                placeholder = { Text(stringResource(id = R.string.search_products)) },
                                onValueChange = {
                                    search = it
                                    viewModel.query(it)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(0.dp),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    containerColor = Color.Transparent
                                )
                            )
                            AnimatedVisibility(visible = loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .height(20.dp)
                                        .width(20.dp),
                                    strokeWidth = 3.dp
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
            contentPadding = innerPadding,
            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ) {
            if (productResults.itemCount == 0) {
                items(10) {
                    ProductCard(
                        product = null,
                        loading = productResults.itemCount == 0,
                        viewModel,
                        navController
                    )
                }
            }
            items(
                items = productResults,
                key = { product -> product.id }
            ) {
                ProductCard(it, loading, viewModel, navController)
            }
        }

    }
}

@Composable
fun ProductCard(
    product: SearchableProduct?,
    loading: Boolean,
    viewModel: SearchViewModel,
    navController: NavHostController
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
                AnimatedVisibility(visible = info?.image != null || loading) {
                    AsyncImage(
                        model = info?.image,
                        contentDescription = stringResource(id = R.string.content_description_product_image),
                        modifier = Modifier
                            .fillMaxHeight()
                            .height(100.dp)
                            .width(100.dp)
                            .align(Alignment.CenterVertically)
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
                    AnimatedVisibility(visible = info?.brandName != null) {
                        Text(
                            text = info?.brandName ?: "",
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

                    AnimatedVisibility(visible = info?.variant != null || info?.quantity != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AnimatedVisibility(visible = info?.variant != null) {
                                Text(
                                    text = info?.variant ?: "",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            AnimatedVisibility(visible = info?.quantity != null) {
                                Text(
                                    text = info?.quantity ?: "",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {

                        PricingBlock(components = bestPrice, loading = loading, local = false)

                        if ((localPrice != null && bestPrice != null) || loading) {
                            Spacer(modifier = Modifier.weight(1.0f))
                        }

                        PricingBlock(components = localPrice, loading = loading, local = true)
                    }

                }
            }

            Row {
                Column {
                    product?.information?.forEach {
                        Text(it.retailer)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { /*TODO*/ },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(),
                        modifier = Modifier
                            .placeholder(
                                visible = loading,
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                highlight = PlaceholderHighlight.fade()
                            )
                            .clip(IconButtonDefaults.filledShape)
                    ) {
                        Icon(Icons.Rounded.Add, stringResource(id = R.string.increase_quantity))
                    }

                    val interactionSource = remember { MutableInteractionSource() }

                    BasicTextField(
                        value = "1",
                        onValueChange = {},
                        modifier = Modifier
                            .width(IntrinsicSize.Min)
                            .placeholder(
                                visible = loading,
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                highlight = PlaceholderHighlight.fade()
                            )
                    ) {
                        TextFieldDefaults.OutlinedTextFieldDecorationBox(
                            value = "1",
                            visualTransformation = VisualTransformation.None,
                            innerTextField = it,
                            singleLine = true,
                            enabled = true,
                            interactionSource = interactionSource,
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                    }

                    IconButton(
                        onClick = { /*TODO*/ },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(),
                        modifier = Modifier.placeholder(
                            visible = loading,
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            highlight = PlaceholderHighlight.fade()
                        )
                    ) {
                        Icon(Icons.Rounded.Remove, stringResource(id = R.string.decrease_quantity))
                    }
                }

                Spacer(modifier = Modifier.weight(1.0f))

                ExtendedFloatingActionButton(
                    onClick = { /*TODO*/ },
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
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
            }

        }
    }
}

@Composable
fun PricingBlock(components: Pair<String, String>?, loading: Boolean, local: Boolean) {
    AnimatedVisibility(visible = components != null || loading) {
        Column {
            Text(
                text = stringResource(id = if (local) R.string.best_local_price else R.string.best_price),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = if (loading) 4.dp else 0.dp)
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier.placeholder(
                    visible = loading,
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    highlight = PlaceholderHighlight.fade()
                )
            )
        }
    }
}