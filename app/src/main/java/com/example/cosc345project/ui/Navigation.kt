package com.example.cosc345project.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cosc345project.R

/**
 * Navigation class
 *
 *
 * @param route
 * @param nameResource
 * @param icon
 */
enum class Navigation(
    val route: String,
    @StringRes val nameResource: Int?,
    val icon: ImageVector?
) {
    SEARCH("search", R.string.search, Icons.Rounded.Search),
    PRODUCT("products/{productId}", null, null),
    SHOPPING_LIST("list", R.string.shopping_list, Icons.Rounded.List);
//    SETTINGS("settings", R.string.settings, Icons.Rounded.Settings);

    companion object {
        val topLevel = arrayOf(SEARCH, SHOPPING_LIST)
    }
}