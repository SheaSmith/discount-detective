package io.github.sheasmith.discountdetective.ui.components.product

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.Store
import com.example.cosc345.shared.models.StorePricingInformation
import io.github.sheasmith.discountdetective.R

/**
 * Function used to create the retailer slot which shows the prices for the products.
 *
 * Includes both the regular price and the discounted price.
 */
@Composable
fun RetailerSlot(
    pricingInformation: StorePricingInformation,
    retailer: Retailer,
    productInformation: RetailerProductInformation
) {
    val store = retailer.stores!!.first { it.id == pricingInformation.store }
    var showRetailer by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .height(70.dp)
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .fillMaxWidth(),

        shape = RoundedCornerShape(8.dp), tonalElevation = 2.dp
    ) {
        Row {


            Row(
                modifier = Modifier
                    .weight(0.8f) // this was changed from 1.0 to make room for the info button
                    .align(Alignment.CenterVertically)
                    .padding(end = 10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                RetailerIcon(retailer = retailer, store = store)

                Spacer(Modifier.width(10.0.dp))

                Text(
                    text = store.name!!, fontSize = 14.sp, lineHeight = 16.sp, maxLines = 3
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(0.2f)
            ) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { showRetailer = !showRetailer }
                        .alpha(0.6f)
                )

                DropdownMenu(expanded = showRetailer, onDismissRequest = { showRetailer = false }) {
                    DropdownMenuItem(text = { Text(retailer.name!!) }, onClick = { }, enabled = false
                    )

                    if (store.latitude != null && store.longitude != null) {
                        DropdownMenuItem(
                            text = {
                                Text("Show on Map")
                            },
                            onClick = {
                                val storeName =
                                    if (retailer.name == store.name) retailer.name else "${retailer.name} ${store.name}"

                                val geoLocation =
                                    Uri.parse("geo:0,0?q=${store.latitude!!},${store.longitude!!}(${storeName})&z=12")

                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = geoLocation
                                }

                                if (intent.resolveActivity(context.packageManager) != null) {
                                    ContextCompat.startActivity(context, intent, null)
                                }
                            },
                        )
                    }
                }

                Spacer(Modifier.width(5.0.dp))
            }

            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
            ) {
                val price = pricingInformation.price?.let {
                    StorePricingInformation.getDisplayPrice(
                        productInformation, it
                    )
                }

                Text(text = price?.let { "${price.first}${price.second}" } ?: "", fontSize = 14.sp)
            }

            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .align(Alignment.CenterVertically)
            ) {
                if (pricingInformation.discountPrice != null) {
                    val price = StorePricingInformation.getDisplayPrice(
                        productInformation, pricingInformation.discountPrice!!
                    )

                    Text(
                        text = "${price.first}${price.second}", fontSize = 14.sp
                    )
                }

                if (pricingInformation.multiBuyPrice != null && pricingInformation.multiBuyQuantity != null) {
                    val price = StorePricingInformation.getDisplayPrice(
                        productInformation, pricingInformation.multiBuyPrice!!
                    )

                    Text(
                        text = stringResource(
                            id = R.string.multibuy_format,
                            pricingInformation.multiBuyQuantity!!,
                            price.first,
                            price.second
                        ), fontSize = 14.sp
                    )
                }

                if (pricingInformation.clubOnly == true) {
                    Text(
                        text = stringResource(
                            id = R.string.club_only,
                        ),

                        lineHeight = 14.sp, fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.RetailerIcon(
    retailer: Retailer, store: Store
) {
    Surface(
        modifier = Modifier
            .padding(start = 5.dp)
            .width(45.dp)
            .height(45.dp)
            .align(Alignment.CenterVertically),

        shape = CircleShape,
        color = Color(if (isSystemInDarkTheme()) retailer.colourDark!! else retailer.colourLight!!)
    ) {

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = retailer.initialism!!,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(if (isSystemInDarkTheme()) retailer.onColourDark!! else retailer.onColourLight!!)
            )
        }

    }
}