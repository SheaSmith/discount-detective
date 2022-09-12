package io.github.sheasmith.discountdetective.ui.components.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cosc345.shared.models.RetailerProductInformation
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder
import io.github.sheasmith.discountdetective.R

/**
 * The shared product title for products.
 *
 * @param info The information to show the title for.
 * @param loading Whether the parent screen is loading or not.
 */
@Composable
fun ProductTitle(
    info: RetailerProductInformation?,
    loading: Boolean,
    applyStyling: Boolean = true
) {
    Column {
        if (info?.brandName != null) {
            Text(
                text = info.brandName ?: "",
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
            fontWeight = if (applyStyling) FontWeight.Bold else null,
            style = if (applyStyling) MaterialTheme.typography.titleLarge else LocalTextStyle.current
        )

        if (info?.variant != null || info?.quantity != null) {
            FlowRow(
                mainAxisSpacing = 4.dp
            ) {
                if (info.variant != null) {
                    Text(
                        text = info.variant ?: "",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                if (info.quantity != null) {
                    Text(
                        text = info.quantity ?: "",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}