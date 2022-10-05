package io.github.sheasmith.discountdetective.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import dagger.hilt.android.AndroidEntryPoint
import io.github.sheasmith.discountdetective.ui.screens.MainScreen
import io.github.sheasmith.discountdetective.ui.theme.DiscountDetectiveTheme
import io.github.sheasmith.discountdetective.ui.utils.DevicePosture
import io.github.sheasmith.discountdetective.ui.utils.isBookPosture
import io.github.sheasmith.discountdetective.ui.utils.isSeparating
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * The activity which calls the compose functions for our app, along with handling device size and posture (for folding devices).
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    /**
     * The function called when the activity is created.
     *
     * @param savedInstanceState The saved instance state.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
@ExperimentalGetImage
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
@ExperimentalGetImage
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
@ExperimentalGetImage
fun AppPreviewDesktop() {
    DiscountDetectiveTheme {
        MainScreen(
            windowSize = WindowWidthSizeClass.Expanded,
            foldingDevicePosture = DevicePosture.NormalPosture
        )
    }
}