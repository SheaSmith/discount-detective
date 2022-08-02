@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cosc345project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cosc345project.R
import com.example.cosc345project.viewmodel.SearchViewModel

@Composable
@Preview
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    var search by remember { mutableStateOf("") }
    val searchResults by viewModel.searchLiveData.observeAsState()

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
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(top = 100.dp)) {
                items(searchResults!!) {
                    ListItem(
                        headlineText = { Text(it.name) },
                        overlineText = { Text(text = it.brandName ?: "") },
                        supportingText = { Text(text = it.variant ?: "") }
                    )
                }
            }

    }
}