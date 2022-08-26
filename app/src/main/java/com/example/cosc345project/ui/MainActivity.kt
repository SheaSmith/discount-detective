package com.example.cosc345project.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.example.cosc345project.ui.screens.MainScreen
import com.example.cosc345project.ui.theme.DiscountDetectiveTheme
import com.example.cosc345project.ui.utils.DevicePosture
import com.example.cosc345project.ui.utils.isBookPosture
import com.example.cosc345project.ui.utils.isSeparating
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * The activity which calls the compose functions for our app, along with handling device size and posture (for folding devices).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Flow of [DevicePosture] that emits every time there's a change in the windowLayoutInfo
         */
        val devicePostureFlow = WindowInfoTracker.getOrCreate(this).windowLayoutInfo(this)
            .flowWithLifecycle(this.lifecycle)
            .map { layoutInfo ->
                val foldingFeature =
                    layoutInfo.displayFeatures
                        .filterIsInstance<FoldingFeature>()
                        .firstOrNull()
                when {
                    isBookPosture(foldingFeature) ->
                        DevicePosture.BookPosture(foldingFeature.bounds)

                    isSeparating(foldingFeature) ->
                        DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

                    else -> DevicePosture.NormalPosture
                }
            }
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = DevicePosture.NormalPosture
            )

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DiscountDetectiveTheme {
                val windowSize = calculateWindowSizeClass(this@MainActivity)
                val devicePosture = devicePostureFlow.collectAsState().value
                MainScreen(windowSize.widthSizeClass, devicePosture)
            }
        }


    }
}

/**
 * A phone preview.
 */
@Preview(showBackground = true)
@Composable
fun AppPreview() {
    DiscountDetectiveTheme {
        MainScreen(
            windowSize = WindowWidthSizeClass.Compact,
            foldingDevicePosture = DevicePosture.NormalPosture
        )
    }
}

/**
 * A preview for a small tablet.
 */
@Preview(showBackground = true, widthDp = 700)
@Composable
fun AppPreviewTablet() {
    DiscountDetectiveTheme {
        MainScreen(
            windowSize = WindowWidthSizeClass.Medium,
            foldingDevicePosture = DevicePosture.NormalPosture
        )
    }
}

/**
 * A preview for a desktop computer.
 */
@Preview(showBackground = true, widthDp = 1000)
@Composable
fun AppPreviewDesktop() {
    DiscountDetectiveTheme {
        MainScreen(
            windowSize = WindowWidthSizeClass.Expanded,
            foldingDevicePosture = DevicePosture.NormalPosture
        )
    }
}