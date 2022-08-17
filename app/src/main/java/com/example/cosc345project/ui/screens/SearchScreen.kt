@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.cosc345project.R
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.ui.components.search.SearchProductCard
import com.example.cosc345project.ui.components.search.SearchTopAppBar
import com.example.cosc345project.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

/**
 * Class for the Search Screen
 *
 * Creates the user interface search screen and links to the database so that users can search
 * for certain foods.
 *
 * @param viewModel
 * @param navController
 */
@Composable
@Preview
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val search by viewModel.searchQuery.collectAsState()
    val retailers by viewModel.retailers.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val searchResults by viewModel.searchLiveData
    val productResults = searchResults.collectAsLazyPagingItems()
    val loading by remember {
        viewModel.loading
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
                            viewModel.setQuery(it)
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        },
                        onFocusChanged = {
                            showSuggestions = it.isFocused
                        },
                        onSearch = {
                            viewModel.query()
                        },
                        onClear = {
                            viewModel.setQuery("", runSearch = true)
                        }
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
            if (showSuggestions && suggestions.isNotEmpty()) {
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
            } else {
                if (productResults.itemCount == 0 || retailers.isEmpty()) {
                    items(10) {
                        SearchProductCard(
                            null,
                            productResults.itemCount == 0,
                            navController,
                            retailers,
                            snackbarHostState,
                            coroutineScope
                        )
                    }
                } else if (retailers.isNotEmpty()) {
                    items(
                        items = productResults,
                        key = { product -> product.first }
                    ) {
                        SearchProductCard(
                            it,
                            loading,
                            navController,
                            retailers,
                            snackbarHostState,
                            coroutineScope
                        )
                    }
                }
            }
        }

    }
}