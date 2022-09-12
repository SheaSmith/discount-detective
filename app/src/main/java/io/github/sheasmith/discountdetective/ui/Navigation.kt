package io.github.sheasmith.discountdetective.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.sheasmith.discountdetective.R

/**
 * The enum which defines all of the navigable screens in the app.
 *
 * @param route The route to use for navigation.
 * @param nameResource Optional, the string resource to use for the screen's name in the top level navigation
 * @param icon Optional, the icon to use in the top level navigation.
 */
enum class Navigation(
    val route: String,
    @StringRes val nameResource: Int?,
    val icon: ImageVector?
) {
    /**
     * The [io.github.sheasmith.discountdetective.ui.screens.SearchScreen].
     */
    SEARCH("search", R.string.search, Icons.Rounded.Search),

    /**
     * The [io.github.sheasmith.discountdetective.ui.screens.ProductScreen].
     */
    PRODUCT("products/{productId}", null, null),

    /**
     * The [io.github.sheasmith.discountdetective.ui.screens.ShoppingListScreen].
     */
    SHOPPING_LIST("list", R.string.shopping_list, Icons.Rounded.List),

    /**
     * The [io.github.sheasmith.discountdetective.ui.screens.SettingsScreen].
     */
    SETTINGS("settings", R.string.settings, Icons.Rounded.Settings),

    /**
     * The [io.github.sheasmith.discountdetective.ui.screens.BarcodeScreen].
     */
    BARCODE_SCANNER("barcode_scanner", null, null);

    companion object {
        /**
         * The array which defines the top level navigation items.
         */
        val topLevel = arrayOf(SEARCH, SHOPPING_LIST, SETTINGS)
    }
}