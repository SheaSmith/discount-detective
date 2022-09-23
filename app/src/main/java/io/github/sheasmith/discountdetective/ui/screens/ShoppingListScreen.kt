@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.sheasmith.discountdetective.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.github.sheasmith.discountdetective.R
import io.github.sheasmith.discountdetective.models.ShoppingListItem
import io.github.sheasmith.discountdetective.ui.components.StatusBarLargeTopAppBar
import io.github.sheasmith.discountdetective.viewmodel.ShoppingListViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

/**
 * The shopping list screen is the definition of the shopping list page in the app, which allows users to add products to a shopping list.
 *
 * @param viewModel The viewmodel for this screen.
 * @param navController The nav controller for navigating between pages.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
@Preview(showBackground = true)
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = hiltViewModel(),
    navController: NavHostController = rememberAnimatedNavController()
) {

    val retailers by viewModel.retailers.collectAsState()
    //productIDs in the shopping list
    val products by viewModel.shoppingList.collectAsState(initial = null)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            StatusBarLargeTopAppBar(
                modifier = Modifier
                    .statusBarsPadding(),
                title = {
                    Text(
                        stringResource(id = R.string.your_shopping_list)
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (products?.isNotEmpty() == true && retailers.isNotEmpty()) {
            val grouped = products!!.groupBy {
                Pair(it.first.retailer!!, it.second.store!!)
            }.mapValues { it.value }

            ProductList(
                grouped = grouped,
                viewModel = viewModel,
                innerPadding = innerPadding,
                retailers = retailers
            )
        } else {
            Text(
                text = stringResource(id = R.string.empty_shopping_list),
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
private fun ProductList(
    grouped: Map<Pair<String, String>, List<Triple<RetailerProductInformation, StorePricingInformation, ShoppingListItem>>>,
    retailers: Map<String, Retailer>,
    viewModel: ShoppingListViewModel,
    innerPadding: PaddingValues
) {
    val data = remember { mutableStateOf(grouped.values.toList().flatten()) }

    val state = rememberReorderableLazyListState(onMove = { (index, _), (index1, _) ->
        data.value = data.value.toMutableList().apply {
            add(index1, removeAt(index))
        }
    })

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = innerPadding,
        modifier = Modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        grouped.forEach { (retailerStore, productsForStore) ->
            stickyHeader {
                Text(
                    text = viewModel.getStoreName(
                        retailerStore.first,
                        retailerStore.second,
                        retailers
                    ) ?: "",
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            //This allows future extension if we want to reorder products
            val dataIdList = data.value.map { it.first.id }
            val productsForStoreId = productsForStore.map { it.first.id }
            val intersectIds = productsForStoreId.intersect(dataIdList.toSet())
            val selection =
                mutableListOf<Triple<RetailerProductInformation, StorePricingInformation, ShoppingListItem>>()
            data.value.forEach {
                if (it.first.id in intersectIds) {
                    selection.add(it)
                }
            }
            items(productsForStore) { shoppingListItem ->
                ReorderableItem(
                    reorderableState = state,
                    key = shoppingListItem
                )
                { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                    ProductCard(
                        shoppingListItem = shoppingListItem,
                        elevation = elevation,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


@Composable
private fun ProductCard(
    shoppingListItem: Triple<RetailerProductInformation, StorePricingInformation, ShoppingListItem>,
    elevation: State<Dp>,
    viewModel: ShoppingListViewModel
) {
    var isChecked by mutableStateOf(shoppingListItem.third.checked)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp) //this for padding
            .shadow(elevation.value)
            .alpha(if (isChecked) 0.5f else 1f),
        colors = CardDefaults.elevatedCardColors(
            disabledContainerColor = Color.Transparent
        ),
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        // master row layout
        Row(
            modifier = Modifier
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.DragHandle,
                contentDescription = stringResource(id = R.string.drag_handle),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxSize(0.1f)
            )
            // product info block
            ProductInfo(product = shoppingListItem)

            // delete button
            IconButton(
                onClick = {
                    viewModel.delete(shoppingListItem.third)
                },
                modifier = Modifier
                    .size(
                        width = 30.dp,
                        height = 30.dp
                    )
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }

            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    shoppingListItem.third.checked = it
                    viewModel.changeShoppingListChecked(shoppingListItem.third)
                    isChecked = it
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProductInfo(product: Triple<RetailerProductInformation, StorePricingInformation, ShoppingListItem>) {
    AsyncImage(
        model = product.first.image,
        contentDescription = stringResource(id = R.string.content_description_product_image),
        modifier = Modifier
            .height(50.dp)
            .width(50.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
    )

    //brand-name, product name and pricing info
    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "${product.third.quantity}x ${
                arrayOf(
                    product.first.brandName,
                    product.first.name,
                    product.first.variant,
                    product.first.quantity
                ).filterNotNull()
                    .joinToString(" ")
            }",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall
        )

        val price = product.second.getDisplayPrice(product.first)
        Text(
            text = "${price.first}${price.second}",
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.titleSmall
        )
    }
}
