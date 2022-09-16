package io.github.sheasmith.discountdetective.ui.components.main

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
@ExperimentalGetImage
fun NavigationWrapper(
    navigationType: NavigationType,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberAnimatedNavController()

    if (navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.background,
                    drawerTonalElevation = 0.dp
                ) {
                    NavigationDrawerContent(
                        navController,
                        navigationType = navigationType
                    )
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
        ) {
            MainContent(navController, navigationType)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    NavigationDrawerContent(
                        navController,
                        onDrawerClicked = {
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        navigationType = navigationType
                    )
                }
            },
            drawerState = drawerState
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