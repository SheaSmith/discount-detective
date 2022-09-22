@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.sheasmith.discountdetective.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

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
                retailers = retailers,
                onCheckedItem = { item, checked ->
                    viewModel.changeShoppingListChecked(item, checked)
                },
                onDeleteItem = { item ->
                    viewModel.delete(item)
                }
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
    innerPadding: PaddingValues,
    onCheckedItem: (ShoppingListItem, Boolean) -> Unit,
    onDeleteItem: (ShoppingListItem) -> Unit
) {

    var list by remember { mutableStateOf(List(5) { it }) }

    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        list = list.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = innerPadding,
        modifier = Modifier.dragContainer(dragDropState),
        state = listState

    ) {
//        grouped.forEach { (retailerStore, productsForStore) ->
//            stickyHeader {
//                Text(
//                    text = viewModel.getStoreName(
//                        retailerStore.first,
//                        retailerStore.second,
//                        retailers
//                    ) ?: "",
//                    textAlign = TextAlign.Left,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(10.dp),
//                    fontWeight = FontWeight.Bold,
//                    style = MaterialTheme.typography.titleMedium
//                )
//            }
        itemsIndexed(list, key = { _, item -> item }) { index, item ->
            DraggableItem(dragDropState, index) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 4.dp else 1.dp)
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = elevation
                    )
                ) {
                    Text(
                        "Item $item",
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun ProductCard(
    product: Triple<RetailerProductInformation, StorePricingInformation, ShoppingListItem>,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    // remember local action state
    var isChecked by rememberSaveable { mutableStateOf(checked) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp) //this for padding
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
            ProductInfo(product = product)

            // delete button
            IconButton(
                onClick = onDelete,
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
                    isChecked = it
                    onCheckedChanged(it)
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

/**
 *
 * From
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/foundation/foundation/integration-tests/foundation-demos/src/main/java/androidx/compose/foundation/demos/LazyColumnDragAndDropDemo.kt
 */
@Composable
fun rememberDragDropState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit
): DragDropState {
    val scope = rememberCoroutineScope()
    val state = remember(lazyListState) {
        DragDropState(
            state = lazyListState,
            onMove = onMove,
            scope = scope
        )
    }
    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }
    return state
}

class DragDropState internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMove: (Int, Int) -> Unit
) {
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

    internal val scrollChannel = Channel<Float>()

    private var draggingItemDraggedDelta by mutableStateOf(0f)
    private var draggingItemInitalOffset by mutableStateOf(0)
    internal val draggingItemOffset: Float
        get() = draggingItemLayoutInfo?.let { item ->
            draggingItemInitalOffset + draggingItemDraggedDelta - item.offset
        } ?: 0f

    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == draggingItemIndex }

    internal var previousIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set
    internal var previousItemOffset = Animatable(0f)
        private set

    internal fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also {
                draggingItemIndex = it.index
                draggingItemInitalOffset = it.offset
            }
    }

    internal fun onDragInterrupted() {
        if (draggingItemIndex != null) {
            previousIndexOfDraggedItem = draggingItemIndex
            val startOffset = draggingItemOffset
            scope.launch {
                previousItemOffset.snapTo(startOffset)
                previousItemOffset.animateTo(
                    0f,
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = 1f
                    )
                )
                previousIndexOfDraggedItem = null
            }
        }
        draggingItemDraggedDelta = 0f
        draggingItemIndex = null
        draggingItemInitalOffset = 0
    }

    internal fun onDrag(offset: Offset) {
        draggingItemDraggedDelta += offset.y

        val draggingItem = draggingItemLayoutInfo ?: return
        val startOffset = draggingItem.offset + draggingItemOffset
        val endOffSet = startOffset + draggingItem.size
        val middleOffSet = startOffset + (endOffSet - startOffset) / 2f

        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            middleOffSet.toInt() in item.offset..item.offsetEnd &&
                    draggingItem.index != item.index
        }
        if (targetItem != null) {
            val scrollToIndex = if (targetItem.index == state.firstVisibleItemIndex) {
                draggingItem.index
            } else if (draggingItem.index == state.firstVisibleItemIndex) {
                targetItem.index
            } else {
                null
            }
            if (scrollToIndex != null) {
                scope.launch {
                    // neutralise automatic keeping the first item
                    state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
                    onMove.invoke(draggingItem.index, targetItem.index)
                }
            } else {
                onMove.invoke(draggingItem.index, targetItem.index)
            }
            draggingItemIndex = targetItem.index
        } else {
            val overscroll = when {
                draggingItemDraggedDelta > 0 ->
                    (endOffSet - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)
                draggingItemDraggedDelta < 0 ->
                    (startOffset - state.layoutInfo.viewportStartOffset).coerceAtLeast(0f)
                else -> 0f
            }
            if (overscroll != 0f) {
                scrollChannel.trySend(overscroll)
            }
        }
    }

    private val LazyListItemInfo.offsetEnd: Int
        get() = this.offset + this.size
}

fun Modifier.dragContainer(dragDropState: DragDropState): Modifier {
    return pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                dragDropState.onDrag(offset = offset)
            },
            onDragStart = { offset -> dragDropState.onDragStart(offset) },
            onDragEnd = { dragDropState.onDragInterrupted() },
            onDragCancel = { dragDropState.onDragInterrupted() }
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun LazyItemScope.DraggableItem(
    dragDropState: DragDropState,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(isDragging: Boolean) -> Unit
) {
    val dragging = index == dragDropState.draggingItemIndex
    val draggingModifier = if (dragging) {
        Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationY = dragDropState.draggingItemOffset
            }
    } else if (index == dragDropState.previousIndexOfDraggedItem) {
        Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationY = dragDropState.previousItemOffset.value
            }
    } else {
        Modifier.animateItemPlacement()
    }
    Column(modifier = modifier.then(draggingModifier)) {
        content(dragging)
    }
}