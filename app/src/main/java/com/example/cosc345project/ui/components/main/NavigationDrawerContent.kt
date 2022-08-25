package com.example.cosc345project.ui.components.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.cosc345project.ui.utils.NavigationType

/**
 * The content of the navigation drawer.
 *
 * @param navController The nav controller to use when navigating between pages.
 * @param modifier The modifier to use for the content of the navigation drawer.
 * @param onDrawerClicked The function to be called when the open drawer button is clicked.
 * @param navigationType The type of navigation that is being used, based on screen size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerContent(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {},
    navigationType: NavigationType
) {
//    Column(
//        modifier
//            .wrapContentWidth()
//            .fillMaxHeight()
//            .background(MaterialTheme.colorScheme.background)
//            .padding(24.dp)
//    ) {
//        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
//        Row(
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = stringResource(id = R.string.app_name).uppercase(),
//                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.primary
//            )
//            AnimatedVisibility(visible = navigationType != NavigationType.PERMANENT_NAVIGATION_DRAWER) {
//                IconButton(onClick = onDrawerClicked) {
//                    Icon(
//                        imageVector = Icons.Rounded.MenuOpen,
//                        contentDescription = stringResource(id = R.string.navigation_drawer)
//                    )
//                }
//            }
//
//        }
//
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        val currentDestination = navBackStackEntry?.destination
//
//        Navigation.topLevel.forEach { screen ->
//            NavigationDrawerItem(
//                icon = {
//                    Icon(
//                        imageVector = screen.icon!!,
//                        contentDescription = null
//                    )
//                },
//                label = {
//                    Text(
//                        text = stringResource(id = screen.nameResource!!),
//                        modifier = Modifier.padding(horizontal = 16.dp)
//                    )
//                },
//                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
//                onClick = { handleNavigationClick(screen, navController) },
//                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
//            )
//        }
//    }
}