@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.cosc345project.R
import com.example.cosc345project.viewmodel.SearchViewModel

@Composable
@Preview
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    var search by remember { mutableStateOf("") }
    val searchResults by viewModel.searchLiveData
    val productResults = searchResults.collectAsLazyPagingItems()
    val loading by viewModel.loading

    Scaffold(
        topBar = {
            Column {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    shape = CircleShape,
                    tonalElevation = 6.dp
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
                                    .padding(start = 16.dp)
                                    .height(8.dp),
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(top = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                items = productResults,
                key = { product -> product.id }
            ) {
                Card(
                    modifier = Modifier
                        .clickable {
                            // TODO
                        }
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        AnimatedVisibility(visible = it!!.image != null) {
                            AsyncImage(
                                model = it.image,
                                contentDescription = stringResource(id = R.string.content_description_product_image),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(100.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                            AnimatedVisibility(visible = it.brandName != null) {
                                Text(text = it.brandName ?: "")
                            }

                            Text(text = it.name)

                            AnimatedVisibility(visible = it.variant != null || it.quantity != null) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    AnimatedVisibility(visible = it.variant != null) {
                                        Text(text = it.variant ?: "")
                                    }

                                    AnimatedVisibility(visible = it.quantity != null) {
                                        Text(text = it.quantity ?: "")
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

    }
}