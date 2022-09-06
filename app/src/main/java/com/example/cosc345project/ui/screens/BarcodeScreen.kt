package com.example.cosc345project.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.cosc345project.R
import com.example.cosc345project.barcode.BarcodeScannerProcessor
import com.example.cosc345project.barcode.camera.GraphicOverlay
import com.example.cosc345project.ui.components.barcode.BarcodeTopBar
import com.example.cosc345project.ui.theme.DiscountDetectiveTheme
import com.example.cosc345project.viewmodel.BarcodeViewModel
import com.google.accompanist.permissions.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.mlkit.common.MlKitException

/**
 * A screen for scanning barcodes.
 *
 * @param viewModel The view model for the barcode screen.
 * @param navController The navigation controller for navigating between pages.
 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
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
        systemUiController.setStatusBarColor(Color.Transparent, darkIcons = false)
        systemUiController.setNavigationBarColor(Color.Transparent, darkIcons = true)

        onDispose {
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
        }
    }

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status is PermissionStatus.Denied) {
        HandlePermissionDenial(cameraPermissionState, navController)
    }

    DiscountDetectiveTheme(darkTheme = true, doNotOverride = true) {
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
                    val result = updatePreview(
                        cameraProvider,
                        previewUseCase,
                        view,
                        lifecycleState,
                        cameraSelector,
                        flashEnabled
                    )

                    if (result != null) {
                        previewUseCase = result.first
                        hasFlash = result.second
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
                    analysisUseCase = updateGraphicOverlay(
                        cameraProvider,
                        analysisUseCase,
                        view,
                        viewModel,
                        lensFacing,
                        lifecycleState,
                        cameraSelector
                    )
                }
            )

            BarcodeTopBar(
                navController = navController,
                hasFlash = hasFlash,
                flashEnabled = flashEnabled,
                setFlash = {
                    flashEnabled = it
                },
                lensFacing = lensFacing,
                setLensFacing = {
                    lensFacing = it
                }
            )

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

@ExperimentalGetImage
private fun updateGraphicOverlay(
    cameraProvider: ProcessCameraProvider?,
    previousAnalysisUseCase: ImageAnalysis?,
    view: GraphicOverlay,
    viewModel: BarcodeViewModel,
    @CameraSelector.LensFacing lensFacing: Int,
    lifecycleState: LifecycleOwner,
    cameraSelector: CameraSelector
): ImageAnalysis? {
    if (cameraProvider != null) {
        if (previousAnalysisUseCase != null) {
            cameraProvider.unbind(previousAnalysisUseCase)
        }

        val imageProcessor = BarcodeScannerProcessor(view.context, viewModel, view)
        val analysisUseCase = ImageAnalysis.Builder().build()

        var needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase.setAnalyzer(
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

        cameraProvider.bindToLifecycle(
            lifecycleState,
            cameraSelector,
            analysisUseCase
        )

        return analysisUseCase
    }

    return null
}

@ExperimentalGetImage
private fun updatePreview(
    cameraProvider: ProcessCameraProvider?,
    previousPreviewUseCase: Preview?,
    view: PreviewView,
    lifecycleState: LifecycleOwner,
    cameraSelector: CameraSelector,
    flashEnabled: Boolean
): Pair<Preview, Boolean>? {
    if (cameraProvider != null) {
        if (previousPreviewUseCase != null) {
            cameraProvider.unbind(previousPreviewUseCase)
        }

        val previewUseCase = Preview.Builder().build()

        previewUseCase.setSurfaceProvider(view.surfaceProvider)

        val camera = cameraProvider.bindToLifecycle(
            lifecycleState,
            cameraSelector,
            previewUseCase
        )

        val hasFlash = camera.cameraInfo.hasFlashUnit()
        camera.cameraControl.enableTorch(flashEnabled)

        return Pair(previewUseCase, hasFlash)
    }

    return null
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun HandlePermissionDenial(
    cameraPermissionState: PermissionState,
    navController: NavController
) {
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
        SideEffect {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}