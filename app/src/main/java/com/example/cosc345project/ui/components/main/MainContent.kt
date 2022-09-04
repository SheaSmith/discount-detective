package com.example.cosc345project.ui.components.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cosc345project.ui.Navigation
import com.example.cosc345project.ui.screens.*
import com.example.cosc345project.ui.utils.NavigationType
import com.example.cosc345project.viewmodel.SearchViewModel
import com.example.cosc345project.viewmodel.ShoppingListViewModel

/**
 * The main content of the page, including the screens themselves.
 *
 * @param navController The nav controller to use when navigating between pages.
 * @param navigationType The type of navigation that is being used, based on screen size.
 * @param onDrawerClicked The function to be called when the open drawer button is clicked.
 */
@Composable
fun MainContent(
    navController: NavHostController,
    navigationType: NavigationType,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
            MainNavigationRail(
                navController,
                onDrawerClicked = onDrawerClicked
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Surface(modifier = Modifier.weight(1.0f)) {
                NavHost(
                    navController = navController,
                    startDestination = Navigation.SEARCH.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Navigation.SEARCH.route) {
                        val parentViewModel = hiltViewModel<SearchViewModel>()
                        SearchScreen(parentViewModel, navController)
                    }
                    composable(Navigation.SHOPPING_LIST.route) {
                        val parentViewModel = hiltViewModel<ShoppingListViewModel>()
                        ShoppingListScreen(parentViewModel, navController)
                    }
                    composable(
                        Navigation.PRODUCT.route,
                        arguments = listOf(navArgument("productId") { type = NavType.StringType })
                    ) {
                        ProductScreen(it.arguments!!.getString("productId")!!, nav = navController)
                    }
                    composable(Navigation.SETTINGS.route) { SettingsScreen() }
                    composable(Navigation.BARCODE_SCANNER.route) {
                        BarcodeScreen()
                    }
                }
            }

            AnimatedVisibility(visible = navigationType == NavigationType.BOTTOM_NAVIGATION) {
                Surface(
                    color = NavigationBarDefaults.containerColor,
                    tonalElevation = NavigationBarDefaults.Elevation
                ) {
                    MainBottomNavigationBar(navController)
                }
            }
        }
    }
}