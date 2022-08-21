package com.example.cosc345project.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosc345project.R
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder

/**
 * PricingBlock function finds and displays the pricing information for each product.
 *
 * Displays both the cheapest price and the cheapest local price so that users have the option of
 * choosing either.
 *
 * @param components Pair of strings containing the pricing information.
 * @param loading Boolean variable for for if loading is finished or not.
 * @param local Boolean variable for if there is a local option available.
 * @param modifier ???
 */
@Composable
fun SearchProductPricingBlock(
    components: Pair<String, String>?,
    loading: Boolean,
    local: Boolean,
    modifier: Modifier = Modifier
) {
    if (components != null || loading) {
        Column(
            modifier = modifier.width(IntrinsicSize.Min),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = if (local) R.string.best_local_price else R.string.best_price),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = if (loading) 4.dp else 0.dp)
                    .fillMaxWidth()
                    .placeholder(
                        visible = loading,
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 38.sp
                        )
                    ) {
                        append(components?.first ?: "$10")
                    }
                    append(components?.second ?: ".00/kg")
                },
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .placeholder(
                        visible = loading,
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        highlight = PlaceholderHighlight.fade()
                    )
                    .width(IntrinsicSize.Max)
            )
        }
    }
}