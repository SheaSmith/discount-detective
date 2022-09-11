package io.github.sheasmith.discountdetective.ui.components.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.sheasmith.discountdetective.R
import io.github.sheasmith.discountdetective.ui.Navigation

/**
 * The navigation rail to use on medium sized devices (e.g. small tablets).
 *
 * @param navController The nav controller to use when navigating between pages.
 * @param onDrawerClicked The function to be called when the open drawer button is clicked.
 */
@Composable
fun MainNavigationRail(
    navController: NavHostController,
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        NavigationRailItem(
            selected = false,
            onClick = onDrawerClicked,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = stringResource(id = R.string.navigation_drawer)
                )
            }
        )

        Navigation.topLevel.forEach { screen ->
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = screen.icon!!,
                        contentDescription = null
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = { handleNavigationClick(screen, navController) }
            )
        }
    }
}