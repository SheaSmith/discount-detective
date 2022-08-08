@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.cosc345project.R
import com.example.cosc345project.settings.indexSettingsDataStore
import com.example.cosc345project.ui.components.StatusBarCenterAlignedTopAppBar
import com.example.cosc345project.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.map

@Composable
@Preview
fun ProductScreen(viewModel: SearchViewModel = hiltViewModel()) {
    var search by remember { mutableStateOf("") }
    val searchResults by viewModel.searchLiveData
    val productResults = searchResults.collectAsLazyPagingItems()
    val loading by remember {
        viewModel.loading
    }
    remember {
        viewModel.query()
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    val indexedBefore by LocalContext.current.indexSettingsDataStore.data.map { it.runBefore }
        .collectAsState(
            initial = false
        )

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
            contentPadding = innerPadding
        ) {
            if (productResults.itemCount == 0) {
                items(10) {
                    ProductCard(it = null, loading = productResults.itemCount == 0)
                }
            }
            items(
                items = productResults,
                key = { product -> product.id }
            ) {
                ProductCard(it, loading)
            }
        }

    }
}
