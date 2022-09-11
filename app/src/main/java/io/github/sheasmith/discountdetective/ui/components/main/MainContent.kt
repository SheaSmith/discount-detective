package io.github.sheasmith.discountdetective.ui.components.main

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import io.github.sheasmith.discountdetective.ui.Navigation
import io.github.sheasmith.discountdetective.ui.screens.*
import io.github.sheasmith.discountdetective.ui.utils.NavigationType
import io.github.sheasmith.discountdetective.viewmodel.SearchViewModel
import io.github.sheasmith.discountdetective.viewmodel.ShoppingListViewModel

/**
 * The main content of the page, including the screens themselves.
 *
 * @param navController The nav controller to use when navigating between pages.
 * @param navigationType The type of navigation that is being used, based on screen size.
 * @param onDrawerClicked The function to be called when the open drawer button is clicked.
 */
@OptIn(ExperimentalAnimationApi::class)
@ExperimentalGetImage
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
                AnimatedNavHost(
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
                        BarcodeScreen(hiltViewModel(), navController)
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