/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cosc345project.fragments

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.cosc345project.R
import com.example.cosc345project.barcode.BarcodeScannerProcessor
import com.example.cosc345project.databinding.FragmentBarcodeScanBinding
import com.example.cosc345project.viewmodel.BarcodeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.base.Objects
import com.google.mlkit.common.MlKitException

/** Live preview demo app for ML Kit APIs using CameraX. */
class BarcodeFragment : Fragment() {

    val viewModel: BarcodeViewModel by viewModels()
    private lateinit var binding: FragmentBarcodeScanBinding

    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: BarcodeScannerProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var currentWorkflowState: BarcodeViewModel.WorkflowState? = null
    private lateinit var promptChipAnimator: AnimatorSet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                bindAllCameraUseCases(false)
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.camera_permission_title)
                    .setMessage(R.string.camera_permission_message)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        binding.root.findNavController().navigateUp()
                    }
            }
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) -> {
                bindAllCameraUseCases(false)
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("BarcodeFragment", "onCreate")
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        binding = FragmentBarcodeScanBinding.inflate(inflater, container, false)

        binding.facingSwitch.setOnClickListener {
            if (cameraProvider == null) {
                return@setOnClickListener
            }
            val newLensFacing =
                if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    it.isSelected = false
                    CameraSelector.LENS_FACING_BACK
                } else {
                    it.isSelected = true
                    CameraSelector.LENS_FACING_FRONT
                }
            val newCameraSelector =
                CameraSelector.Builder().requireLensFacing(newLensFacing).build()
            try {
                if (cameraProvider!!.hasCamera(newCameraSelector)) {
                    Log.d("BarcodeFragment", "Set facing to $newLensFacing")
                    lensFacing = newLensFacing
                    cameraSelector = newCameraSelector
                    bindAllCameraUseCases(false)
                    return@setOnClickListener
                }
            } catch (e: CameraInfoUnavailableException) {
                // Falls through
            }
            Toast
                .makeText(
                    context,
                    "This device does not have lens with facing: $newLensFacing",
                    Toast.LENGTH_SHORT
                )
                .show()
        }

        binding.flashButton.setOnClickListener {
            if (it.isSelected) {
                bindAllCameraUseCases(false)
            } else {
                bindAllCameraUseCases(true)
            }
        }

        binding.closeButton.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }

        viewModel.processCameraProvider.observe(viewLifecycleOwner) {
            cameraProvider = it
            bindAllCameraUseCases(false)
        }

        promptChipAnimator = (AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.bottom_prompt_chip_enter
        ) as AnimatorSet).apply {
            setTarget(binding.bottomPromptChip)
        }

        setUpWorkflowModel()

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        imageProcessor?.run { this.stop() }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run { this.stop() }
    }

    private fun bindAllCameraUseCases(flash: Boolean) {
        if (this::binding.isInitialized) {
            binding.flashButton.isSelected = flash
        }

        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase(flash)
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase(flash: Boolean) {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
//        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
//        if (targetResolution != null) {
//            builder.setTargetResolution(targetResolution)
//        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(binding.previewView.surfaceProvider)
        val camera = cameraProvider!!.bindToLifecycle(
            this,
            cameraSelector!!,
            previewUseCase
        )

        binding.flashButton.visibility = if (camera.cameraInfo.hasFlashUnit()) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        camera.cameraControl.enableTorch(flash)
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor =
            try {
                BarcodeScannerProcessor(requireContext(), viewModel, binding.graphicOverlay)
            } catch (e: Exception) {
                Log.e("BarcodeFragment", "Can not create image processor", e)
                Toast.makeText(
                    context,
                    "Can not create image processor: " + e.localizedMessage,
                    Toast.LENGTH_LONG
                )
                    .show()
                return
            }

        val builder = ImageAnalysis.Builder()
//        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
//        if (targetResolution != null) {
//            builder.setTargetResolution(targetResolution)
//        }
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(requireContext())
        ) { imageProxy: ImageProxy ->
            if (needUpdateGraphicOverlayImageSourceInfo) {
                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                    binding.graphicOverlay.setImageSourceInfo(
                        imageProxy.width,
                        imageProxy.height,
                        isImageFlipped
                    )
                } else {
                    binding.graphicOverlay.setImageSourceInfo(
                        imageProxy.height,
                        imageProxy.width,
                        isImageFlipped
                    )
                }
                needUpdateGraphicOverlayImageSourceInfo = false
            }
            try {
                imageProcessor!!.processImageProxy(imageProxy, binding.graphicOverlay)
            } catch (e: MlKitException) {
                Log.e(
                    "BarcodeFragment",
                    "Failed to process image. Error: " + e.localizedMessage
                )
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        cameraProvider!!.bindToLifecycle(
            this,
            cameraSelector!!,
            analysisUseCase
        )
    }

    private fun setUpWorkflowModel() {
        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        viewModel.workflowState.observe(viewLifecycleOwner, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            Log.d("BarcodeFragment", "Current workflow state: ${currentWorkflowState!!.name}")

            val wasPromptChipGone = binding.bottomPromptChip.visibility == View.GONE

            when (workflowState) {
                BarcodeViewModel.WorkflowState.DETECTING -> {
                    binding.bottomPromptChip.visibility = View.VISIBLE
                    binding.bottomPromptChip.setText(R.string.prompt_point_at_a_barcode)
                }
                BarcodeViewModel.WorkflowState.CONFIRMING -> {
                    binding.bottomPromptChip.visibility = View.VISIBLE
                    binding.bottomPromptChip.setText(R.string.prompt_move_camera_closer)
                }
                BarcodeViewModel.WorkflowState.SEARCHING -> {
                    binding.bottomPromptChip.visibility = View.VISIBLE
                    binding.bottomPromptChip.setText(R.string.prompt_searching)
                }
                BarcodeViewModel.WorkflowState.DETECTED, BarcodeViewModel.WorkflowState.SEARCHED -> {
                    binding.bottomPromptChip.visibility = View.GONE
                }
                else -> binding.bottomPromptChip.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation =
                wasPromptChipGone && binding.bottomPromptChip.visibility == View.VISIBLE
            promptChipAnimator.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        viewModel.detectedBarcode.observe(viewLifecycleOwner) { barcode ->
            if (barcode != null) {

            }
        }
    }
}