package com.example.cosc345project.ui.components.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.R
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder

@Composable
fun ProductTitle(info: RetailerProductInformation?, loading: Boolean) {
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
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleLarge
    )

    if (info?.variant != null || info?.quantity != null) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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