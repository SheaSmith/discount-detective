package com.example.cosc345project.ui.components.barcode

import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LensFacing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cosc345project.R

/**
 * The top bar in the barcode scanning screen.
 *
 * @param navController The navigation controller for navigating between pages.
 * @param hasFlash Whether the camera has flash support or not.
 * @param flashEnabled Whether the flash is currently enabled.
 * @param setFlash A function to be called whenever the flash is enabled or disabled.
 * @param lensFacing The current direction of the lens.
 * @param setLensFacing A function to be called whenever the lens changes.
 */
@Composable
fun BoxScope.BarcodeTopBar(
    navController: NavController,
    hasFlash: Boolean,
    flashEnabled: Boolean,
    setFlash: (Boolean) -> Unit,
    @LensFacing lensFacing: Int,
    setLensFacing: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconModifier = Modifier
            .size(48.dp)
            .padding(12.dp)

        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = stringResource(id = R.string.back),
            modifier = iconModifier
                .clickable {
                    navController.navigateUp()
                },
            tint = Color.White,
        )

        Row {
            AnimatedVisibility(visible = hasFlash) {
                Icon(
                    if (flashEnabled) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                    contentDescription = stringResource(id = R.string.toggle_flash),
                    modifier = iconModifier
                        .clickable {
                            setFlash(!flashEnabled)
                        },
                    tint = Color.White
                )
            }

            Icon(
                if (lensFacing == CameraSelector.LENS_FACING_BACK) Icons.Rounded.PhotoCameraFront else Icons.Rounded.PhotoCameraBack,
                contentDescription = stringResource(id = R.string.switch_camera),
                modifier = iconModifier
                    .clickable {
                        setLensFacing(
                            if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                                CameraSelector.LENS_FACING_BACK
                            } else {
                                CameraSelector.LENS_FACING_FRONT
                            }
                        )
                    },
                tint = Color.White
            )
        }
    }
}