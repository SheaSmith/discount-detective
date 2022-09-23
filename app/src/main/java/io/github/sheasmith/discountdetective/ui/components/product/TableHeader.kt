package io.github.sheasmith.discountdetective.ui.components.product

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sheasmith.discountdetective.R

/**
 * Function used to create the headers (local/non-local, retailer, price, discount price) above the
 * lists of cards with their retailer and price.
 *
 * @param local Whether or not the section is for local products.
 */
@Composable
fun TableHeader(local: Boolean) {
    Text(
        text = stringResource(id = if (local) R.string.local_prices else R.string.non_local_prices),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 16.dp, bottom = 4.dp)

    )

    Row(
        modifier = Modifier.padding(all = 10.dp),
        verticalAlignment = Alignment.Bottom
    )
    {
        Text(
            text = stringResource(id = R.string.retailer),
            modifier = Modifier.weight(1.0f),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(id = R.string.price),
            modifier = Modifier.weight(0.5f),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(id = R.string.discount_price),
            modifier = Modifier.weight(0.5f),
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}