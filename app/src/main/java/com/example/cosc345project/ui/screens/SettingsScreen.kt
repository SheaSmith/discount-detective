package com.example.cosc345project.ui.screens

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.cosc345project.databinding.FragmentContainerSettingsBinding

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
