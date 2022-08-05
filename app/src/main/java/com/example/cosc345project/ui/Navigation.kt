package com.example.cosc345project.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cosc345project.R

enum class Navigation(val route: String, @StringRes val nameResource: Int, val icon: ImageVector) {
    SEARCH("products", R.string.search, Icons.Rounded.Search),
    SHOPPING_LIST("list", R.string.shopping_list, Icons.Rounded.List);
//    SETTINGS("settings", R.string.settings, Icons.Rounded.Settings);

    companion object {
        val topLevel = arrayOf(SEARCH, SHOPPING_LIST)
    }
}