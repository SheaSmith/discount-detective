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
package io.github.sheasmith.discountdetective.viewmodel

import android.app.Application
import android.util.Log
import androidx.annotation.MainThread
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.ExecutionException

/** View model for interacting with CameraX.  */
class BarcodeViewModel
/**
 * Create an instance which interacts with the camera service via the given application context.
 */
    (application: Application) : AndroidViewModel(application) {
    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null

    /**
     * The camera provider for the scanner.
     */
    val processCameraProvider: LiveData<ProcessCameraProvider>
        get() {
            if (cameraProviderLiveData == null) {
                cameraProviderLiveData = MutableLiveData()
                val cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication())
                cameraProviderFuture.addListener(
                    {
                        try {
                            cameraProviderLiveData!!.setValue(cameraProviderFuture.get())
                        } catch (e: ExecutionException) {
                            // Handle any errors (including cancellation) here.
                            Log.e(
                                TAG,
                                "Unhandled exception",
                                e
                            )
                        } catch (e: InterruptedException) {
                            Log.e(
                                TAG,
                                "Unhandled exception",
                                e
                            )
                        }
                    },
                    ContextCompat.getMainExecutor(getApplication())
                )
            }
            return cameraProviderLiveData!!
        }

    /**
     * The current state of the scanner.
     */
    val workflowState: MutableLiveData<WorkflowState> = MutableLiveData<WorkflowState>()

    /**
     * The current detected barcode
     */
    val detectedBarcode: MutableLiveData<Barcode> = MutableLiveData<Barcode>()

    /**
     * State set of the application workflow.
     */
    enum class WorkflowState {
        /**
         * A barcode has been detected.
         */
        DETECTED
    }

    /**
     * Set the current state of the workflow.
     */
    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        this.workflowState.value = workflowState
    }

    companion object {
        private const val TAG = "CameraXViewModel"
    }
}