package com.example.cosc345project.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.cosc345project.ui.utils.NavigationType
import kotlinx.coroutines.launch

/**
 * The wrapper used to create the drawer navigation.
 *
 * @param navigationType The type of navigation that is being used, based on screen size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper(
    navigationType: NavigationType,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()

    if (navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                NavigationDrawerContent(
                    navController,
                    navigationType = navigationType
                )
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
        ) {
            MainContent(navController, navigationType)
        }
    } else {
        // Apparently we need the explicit type so Android Studio doesn't try to say it's not needed
        @Suppress("RedundantExplicitType") var modifier: Modifier = Modifier

        if (navigationType != NavigationType.BOTTOM_NAVIGATION) {
            modifier = modifier.navigationBarsPadding()
        }

        ModalNavigationDrawer(
            drawerContent = {
                NavigationDrawerContent(
                    navController,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    navigationType = navigationType
                )
            },
            drawerState = drawerState,
            modifier = modifier
        ) {
            MainContent(
                navController,
                navigationType,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}