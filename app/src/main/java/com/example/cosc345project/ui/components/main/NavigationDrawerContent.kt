package com.example.cosc345project.ui.components.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MenuOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cosc345project.R
import com.example.cosc345project.ui.Navigation
import com.example.cosc345project.ui.utils.NavigationType

/**
 * NavigationDrawerContent
 *
 *
 * @param navController
 * @param modifier
 * @param onDrawerClicked
 * @param navigationType
 */
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
                onClick = { handleNavigationClick(screen, navController) },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
            )
        }
    }
}