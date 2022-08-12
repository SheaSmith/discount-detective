package com.example.cosc345project.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MenuOpen
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cosc345project.R
import com.example.cosc345project.ui.Navigation
import com.example.cosc345project.ui.utils.DevicePosture
import com.example.cosc345project.ui.utils.NavigationType
import com.example.cosc345project.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

// Based on https://github.com/googlecodelabs/android-compose-codelabs/blob/end/AdaptiveUiCodelab/app/src/main/java/com/example/reply/ui/ReplyApp.kt
@Composable
fun MainScreen(
    windowSize: WindowWidthSizeClass,
    foldingDevicePosture: DevicePosture
) {
    val navigationType: NavigationType

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = NavigationType.BOTTOM_NAVIGATION
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = NavigationType.NAVIGATION_RAIL
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                NavigationType.NAVIGATION_RAIL
            } else {
                NavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }
        else -> {
            navigationType = NavigationType.BOTTOM_NAVIGATION
        }
    }

    NavigationWrapper(navigationType)

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationWrapper(
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
            }, modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MainContent(navController, navigationType)
        }
    } else {
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
                    composable(Navigation.SHOPPING_LIST.route) { ShoppingListScreen() }
                    composable(
                        Navigation.PRODUCT.route,
                        arguments = listOf(navArgument("productId") { type = NavType.StringType })
                    ) { ProductScreen() }
//                    composable(Navigation.SETTINGS.route) { Text(text = "Settings") }
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
                onClick = { handleClick(screen, navController) }
            )
        }
    }
}

private fun handleClick(screen: Navigation, navHostController: NavHostController) {
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

@Composable
fun MainBottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        Navigation.topLevel.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon!!,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = screen.nameResource!!)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = { handleClick(screen, navController) }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerContent(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {},
    navigationType: NavigationType
) {
    Column(
        modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            AnimatedVisibility(visible = navigationType != NavigationType.PERMANENT_NAVIGATION_DRAWER) {
                IconButton(onClick = onDrawerClicked) {
                    Icon(
                        imageVector = Icons.Rounded.MenuOpen,
                        contentDescription = stringResource(id = R.string.navigation_drawer)
                    )
                }
            }

        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Navigation.topLevel.forEach { screen ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = screen.icon!!,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = screen.nameResource!!),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = { handleClick(screen, navController) },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
            )
        }
    }
}
