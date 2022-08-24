package com.example.cosc345project.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cosc345project.R

/**
 * The enum which contains all of the possible navigation routes.
 *
 * @param route The route to use when navigating.
 * @param nameResource The string resource of the name, if in the top level navigation.
 * @param icon The icon to use, if in the top level navigation.
 */
enum class Navigation(
    val route: String,
    @StringRes val nameResource: Int?,
    val icon: ImageVector?
) {
    /**
     * The search screen.
     */
    SEARCH("search", R.string.search, Icons.Rounded.Search),

    /**
     * The view individual product screen.
     */
    PRODUCT("products/{productId}", null, null),

    /**
     * The shopping list screen.
     */
    SHOPPING_LIST("list", R.string.shopping_list, Icons.Rounded.List);

    /**
     * The settings screen.
     */
//    SETTINGS("settings", R.string.settings, Icons.Rounded.Settings);

    companion object {
        /**
         * The top level navigation that is shown in the main navigation areas.
         */
        val topLevel = arrayOf(SEARCH, SHOPPING_LIST)
    }
}