package io.github.sheasmith.discountdetective.ui.components.search

import android.content.pm.PackageManager
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * A modified version of the top app bar that contains a search form.
 *
 * @param search The search query.
 * @param loading Boolean variable for whether or not the screens finished loading.
 * @param onValueChange The function called whenever the search query has changed.
 * @param onFocusChanged The function called whenever the search field has its focus changed.
 * @param onSearch The function called when the user selects a suggestion, or clicks the search button.
 * @param onClear The function called when the user clears the search.
 */
@Composable
fun SearchTopAppBar(
    search: String,
    loading: Boolean,
    hasAppSearchLoaded: Boolean?,
    onValueChange: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit,
    onSearch: (Any?) -> Unit,
    onClear: () -> Unit,
    onScanBarcode: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
        shape = CircleShape,
        tonalElevation = 16.dp
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Contents(
                    search = search,
                    hasAppSearchLoaded = hasAppSearchLoaded,
                    onValueChange = onValueChange,
                    onFocusChanged = onFocusChanged,
                    onSearch = onSearch,
                    onClear = onClear,
                    onScanBarcode = onScanBarcode
                )
            }

            if (loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.Contents(
    search: String,
    hasAppSearchLoaded: Boolean?,
    onValueChange: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit,
    onSearch: (Any?) -> Unit,
    onClear: () -> Unit,
    onScanBarcode: () -> Unit
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
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier
            .weight(1f)
            .padding(0.dp)
            .onFocusChanged(onFocusChanged)
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KEYCODE_ENTER) {
                    onSearch(null)
                    true
                } else {
                    false
                }
            },
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = onSearch
        ),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            containerColor = Color.Transparent
        )
    )
    AnimatedVisibility(visible = search.isNotEmpty()) {
        IconButton(onClick = onClear) {
            Icon(
                Icons.Rounded.Close,
                stringResource(id = R.string.content_description_clear_search)
            )
        }
    }
    AnimatedVisibility(
        visible = search.isEmpty() &&
                hasAppSearchLoaded == true &&
                LocalContext.current.packageManager.hasSystemFeature(
                    PackageManager.FEATURE_CAMERA
                )
    ) {
        IconButton(onClick = onScanBarcode) {
            Icon(
                painterResource(id = R.drawable.ic_barcode_scanner),
                stringResource(id = R.string.scan_barcode)
            )
        }
    }
}