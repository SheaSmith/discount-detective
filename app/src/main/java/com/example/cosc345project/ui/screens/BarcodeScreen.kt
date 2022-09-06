package com.example.cosc345project.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.cosc345project.R
import com.example.cosc345project.barcode.BarcodeScannerProcessor
import com.example.cosc345project.barcode.camera.GraphicOverlay
import com.example.cosc345project.ui.theme.DiscountDetectiveTheme
import com.example.cosc345project.viewmodel.BarcodeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.mlkit.common.MlKitException

/**
 * Class for the Settings Screen.
 *
 * Creates the user interface setting screen to allow users to _.
 *
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@ExperimentalGetImage
@Composable
fun BarcodeScreen(viewModel: BarcodeViewModel, navController: NavController) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    var flashEnabled by remember { mutableStateOf(false) }
    var hasFlash by remember { mutableStateOf(true) }
    val cameraProvider by viewModel.processCameraProvider.observeAsState()
    val workflowState by viewModel.workflowState.observeAsState()
    var previewUseCase: Preview? = null
    var analysisUseCase: ImageAnalysis? = null
    var imageProcessor: BarcodeScannerProcessor
    var needUpdateGraphicOverlayImageSourceInfo: Boolean
    val lifecycleState = LocalLifecycleOwner.current
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    viewModel.detectedBarcode.observe(lifecycleState) {
        if (it != null) {
            navController.previousBackStackEntry?.savedStateHandle?.set("barcode", it.rawValue)
            navController.navigateUp()
        }
    }



    cameraProvider?.unbindAll()

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)

        onDispose {
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
        }
    }

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status is PermissionStatus.Denied) {
        if (cameraPermissionState.status.shouldShowRationale) {
            AlertDialog(
                onDismissRequest = { navController.navigateUp() },
                confirmButton = {
                    TextButton(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { navController.navigateUp() }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                icon = { Icon(Icons.Rounded.Warning, contentDescription = null) },
                title = { Text(text = stringResource(id = R.string.camera_permission_title)) },
                text = { Text(text = stringResource(id = R.string.camera_permission_message)) }
            )
        } else {
            try {
                cameraPermissionState.launchPermissionRequest()
            } catch (e: IllegalStateException) {
                // Do nothing
            }
        }

    }

    DiscountDetectiveTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .statusBarsPadding()
        ) {
            AndroidView(
                factory = {
                    PreviewView(it)
                },
                update = { view ->
                    if (cameraProvider != null) {
                        if (previewUseCase != null) {
                            cameraProvider?.unbind(previewUseCase)
                        }

                        previewUseCase = Preview.Builder().build()

                        previewUseCase?.setSurfaceProvider(view.surfaceProvider)

                        val camera = cameraProvider?.bindToLifecycle(
                            lifecycleState,
                            cameraSelector,
                            previewUseCase
                        )

                        hasFlash = camera?.cameraInfo?.hasFlashUnit()!!
                        camera.cameraControl.enableTorch(flashEnabled)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            AndroidView(
                factory = { context ->
                    GraphicOverlay(context, null)
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    if (cameraProvider != null) {
                        if (analysisUseCase != null) {
                            cameraProvider?.unbind(analysisUseCase)
                        }

                        imageProcessor = BarcodeScannerProcessor(view.context, viewModel, view)
                        analysisUseCase = ImageAnalysis.Builder().build()

                        needUpdateGraphicOverlayImageSourceInfo = true

                        analysisUseCase?.setAnalyzer(
                            ContextCompat.getMainExecutor(view.context),
                        ) { imageProxy ->
                            if (needUpdateGraphicOverlayImageSourceInfo) {
                                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                                if (rotationDegrees == 0 || rotationDegrees == 180) {
                                    view.setImageSourceInfo(
                                        imageProxy.width,
                                        imageProxy.height,
                                        isImageFlipped
                                    )
                                } else {
                                    view.setImageSourceInfo(
                                        imageProxy.height,
                                        imageProxy.width,
                                        isImageFlipped
                                    )
                                }
                                needUpdateGraphicOverlayImageSourceInfo = false
                            }
                            try {
                                imageProcessor.processImageProxy(imageProxy, view)
                            } catch (e: MlKitException) {
                                Log.e(
                                    "BarcodeFragment",
                                    "Failed to process image. Error: " + e.localizedMessage
                                )
                                Toast.makeText(view.context, e.localizedMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        cameraProvider?.bindToLifecycle(
                            lifecycleState,
                            cameraSelector,
                            analysisUseCase
                        )
                    }
                }
            )

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
                                    flashEnabled = !flashEnabled
                                },
                            tint = Color.White
                        )
                    }

                    Icon(
                        if (lensFacing == CameraSelector.LENS_FACING_BACK) Icons.Rounded.PhotoCameraFront else Icons.Rounded.PhotoCameraBack,
                        contentDescription = stringResource(id = R.string.switch_camera),
                        modifier = iconModifier
                            .clickable {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                                    CameraSelector.LENS_FACING_BACK
                                } else {
                                    CameraSelector.LENS_FACING_FRONT
                                }
                            },
                        tint = Color.White
                    )
                }
            }

            AnimatedVisibility(
                visible = workflowState != BarcodeViewModel.WorkflowState.DETECTED,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(text = stringResource(id = R.string.prompt_point_at_a_barcode))
                    },
                    modifier = Modifier
                        .padding(24.dp)
                )
            }

        }
    }

}
