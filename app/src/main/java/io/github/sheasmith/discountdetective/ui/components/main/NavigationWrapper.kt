package io.github.sheasmith.discountdetective.ui.components.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.github.sheasmith.discountdetective.ui.utils.NavigationType
import kotlinx.coroutines.launch

/**
 * The wrapper used to create the drawer navigation.
 *
 * @param navigationType The type of navigation that is being used, based on screen size.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NavigationWrapper(
    navigationType: NavigationType,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberAnimatedNavController()

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