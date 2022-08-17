package com.example.cosc345project.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import com.example.cosc345project.ui.components.main.NavigationWrapper
import com.example.cosc345project.ui.utils.DevicePosture
import com.example.cosc345project.ui.utils.NavigationType


/**
 * Class for the Main Screen
 *
 * Creates the base screen
 *
 * @param windowSize
 * @param foldingDevicePosture
 */
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

