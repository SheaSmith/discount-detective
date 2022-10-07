@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.sheasmith.discountdetective.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.SignalWifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.github.sheasmith.discountdetective.R
import io.github.sheasmith.discountdetective.ui.Navigation
import io.github.sheasmith.discountdetective.ui.components.StatusBarCenterAlignedTopAppBar
import io.github.sheasmith.discountdetective.ui.components.search.ErrorUi
import io.github.sheasmith.discountdetective.ui.components.search.SearchProductCard
import io.github.sheasmith.discountdetective.ui.components.search.SearchTopAppBar
import io.github.sheasmith.discountdetective.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

/**
 * Function for the Search Screen
 *
 * Creates the user interface search screen and links elements of the UI to the database so that
 * users can search for certain foods.
 *
 * @param viewModel Instance of the SearchViewModel class (see [SearchViewModel])
 * @param navController Instance of the nav controller for navigation class.
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navController: NavHostController = rememberAnimatedNavController()
) {
    val search by viewModel.searchQuery.collectAsState()
    val retailers by viewModel.retailers.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val searchResults by viewModel.searchLiveData
    val productResults = searchResults.collectAsLazyPagingItems()
    var loading by remember {
        mutableStateOf(false)
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val focusManager = LocalFocusManager.current
    var showSuggestions by remember {
        viewModel.showSuggestions
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val hasIndexed by viewModel.hasIndexed.observeAsState()
    val hasUsedBarcode by viewModel.hasUsedBarcodeScan.observeAsState()
    val region by viewModel.region.observeAsState()

    val barcodeResult = (navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
        "barcode"
    ) ?: return).observeAsState()

    barcodeResult.value?.let {
        navController.currentBackStackEntry?.savedStateHandle?.set("barcode", null)
        viewModel.setQuery(it, true)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            StatusBarCenterAlignedTopAppBar(
                modifier = Modifier
                    .statusBarsPadding(),
                title = {
                    SearchTopAppBar(
                        search = search,
                        loading = loading,
                        onValueChange = {
                            val query = it.filter { letter -> letter != '\n' }
                            viewModel.setQuery(query)
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        },
                        onFocusChanged = {
                            showSuggestions = it.isFocused
                        },
                        onSearch = {
                            viewModel.query()
                            focusManager.clearFocus()
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        },
                        onClear = {
                            viewModel.setQuery("", runSearch = true)
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        },
                        onScanBarcode = {
                            navController.navigate(Navigation.BARCODE_SCANNER.route)
                        },
                        hasAppSearchLoaded = hasIndexed
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                innerPadding.calculateTopPadding() + 4.dp,
                innerPadding.calculateEndPadding(
                    LocalLayoutDirection.current
                ),
                innerPadding.calculateBottomPadding() + 8.dp
            ),
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { focusManager.clearFocus() }
                    )
                },
            state = listState
        ) {
            val loadState = productResults.loadState.refresh
            loading = loadState == LoadState.Loading

            if (showSuggestions && suggestions.isNotEmpty()) {
                suggestionsList(
                    suggestions = suggestions,
                    viewModel = viewModel,
                    focusManager = focusManager
                )
            } else {
                indexingCard(hasIndexed)
                hasUsedBarcodeCard(hasIndexed, hasUsedBarcode)

                if (loading) {
                    items(10) {
                        SearchProductCard(
                            null,
                            productResults.itemCount == 0,
                            navController,
                            retailers,
                            snackbarHostState,
                            coroutineScope,
                            region = region!!
                        )
                    }
                } else if (retailers.isNotEmpty() && productResults.itemCount != 0) {
                    items(
                        items = productResults,
                        key = { (first, _) -> first }
                    ) {
                        SearchProductCard(
                            it,
                            loading,
                            navController,
                            retailers,
                            snackbarHostState,
                            coroutineScope,
                            onAddToShoppingList = { productId, retailerProductInfoId, storeId, quantity ->
                                viewModel.addToShoppingList(
                                    productId,
                                    retailerProductInfoId,
                                    storeId,
                                    quantity
                                )
                            },
                            region = region!!

                        )
                    }
                } else if (loadState !is LoadState.Error && retailers.isNotEmpty()) {
                    noResultsError()
                } else {
                    noInternetError(viewModel)
                }
            }
        }

    }
}

private fun LazyListScope.noInternetError(viewModel: SearchViewModel) {
    item {
        ErrorUi(
            title = R.string.no_internet,
            description = R.string.no_internet_description,
            icon = Icons.Rounded.SignalWifiOff,
            onRetry = {
                viewModel.query()
            })
    }
}

private fun LazyListScope.suggestionsList(
    suggestions: List<String>,
    viewModel: SearchViewModel,
    focusManager: FocusManager
) {
    item {
        Text(
            text = stringResource(R.string.search_suggestions),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
    items(
        items = suggestions
    ) {
        ListItem(
            headlineText = { Text(text = it) },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable {
                    viewModel.setQuery(it, true)
                    focusManager.clearFocus()
                }
        )
    }
}

private fun LazyListScope.indexingCard(hasIndexed: Boolean?) {
    item {
        AnimatedVisibility(visible = hasIndexed == false) {
            InformationCard(stringRes = R.string.still_indexing)
        }
    }
}

private fun LazyListScope.hasUsedBarcodeCard(hasIndexed: Boolean?, hasUsedBarcode: Boolean?) {
    item {
        AnimatedVisibility(visible = hasIndexed == true && hasUsedBarcode != true) {
            InformationCard(stringRes = R.string.has_used_barcode)
        }
    }
}

@Composable
private fun InformationCard(@StringRes stringRes: Int) {
    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Info,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = stringResource(stringRes))
        }
    }
}

private fun LazyListScope.noResultsError() {
    item {
        ErrorUi(
            title = R.string.no_results,
            description = R.string.no_results_description,
            icon = Icons.Rounded.SearchOff
        )
    }
}