package com.example.cosc345project.ui.components.main

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.cosc345project.ui.Navigation

/**
 * handleClick
 *
 * Is private because:
 *
 * @param screen
 * @param navHostController
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
        restoreState = true
    }
}