package com.example.cosc345project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.cosc345project.databinding.FragmentContainerBarcodeScanBinding
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Class for the Settings Screen.
 *
 * Creates the user interface setting screen to allow users to _.
 *
 */
@Composable
@Preview
fun BarcodeScreen() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)

        onDispose {
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
        }
    }

    AndroidViewBinding(
        FragmentContainerBarcodeScanBinding::inflate,
        modifier = Modifier
            .background(Color.Black)
            .statusBarsPadding()
    )
}
