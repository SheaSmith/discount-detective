package io.github.sheasmith.discountdetective.ui.screens

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import io.github.sheasmith.discountdetective.databinding.FragmentContainerSettingsBinding

/**
 * Class for the Settings Screen.
 *
 * Creates the user interface setting screen to allow users to _.
 *
 */
@Composable
@Preview
fun SettingsScreen() {
    AndroidViewBinding(
        FragmentContainerSettingsBinding::inflate,
        modifier = Modifier.statusBarsPadding()
    )
}
