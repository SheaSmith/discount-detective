package io.github.sheasmith.discountdetective.ui.components.main

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import io.github.sheasmith.discountdetective.ui.Navigation

/**
 * Handle navigation click, essentially move to the new page.
 *
 * @param screen The screen to move to.
 * @param navHostController The nav host controller to navigate to the new page with.
 */
internal fun handleNavigationClick(screen: Navigation, navHostController: NavHostController) {
    navHostController.navigate(screen.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navHostController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = false
    }
}