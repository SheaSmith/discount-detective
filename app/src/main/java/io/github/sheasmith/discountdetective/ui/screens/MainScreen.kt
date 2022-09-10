package io.github.sheasmith.discountdetective.ui.screens

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import io.github.sheasmith.discountdetective.ui.components.main.NavigationWrapper
import io.github.sheasmith.discountdetective.ui.utils.DevicePosture
import io.github.sheasmith.discountdetective.ui.utils.NavigationType

/**
 * Function for the Main Screen
 *
 * Creates the main screen as a composable function, changing the navigation options on
 * the app depending on the window size on the phone and the device posture. This allows
 * us to cater our UI to a wide variety of phones and window sizes.
 *
 * @param windowSize Size of the window our app is in.
 * @param foldingDevicePosture Status of the folding device - to make our app fold aware.
 */
// Based on https://github.com/googlecodelabs/android-compose-codelabs/blob/end/AdaptiveUiCodelab/app/src/main/java/com/example/reply/ui/ReplyApp.kt
@Composable
@ExperimentalGetImage
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

