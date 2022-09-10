package io.github.sheasmith.discountdetective.ui.components.search

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A component that shows whenever there is an error with the search (for example, no internet or
 * no results found).
 *
 * @param title The string resource to use for the error title.
 * @param description The string resource to use for the error description.
 * @param icon The icon to use for the error.
 * @param onRetry If specified, a retry button will display, and this is the function called when it
 * is pressed.
 */
@Composable
fun SearchError(
    @StringRes title: Int,
    @StringRes description: Int,
    icon: ImageVector,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = description),
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            TextButton(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}