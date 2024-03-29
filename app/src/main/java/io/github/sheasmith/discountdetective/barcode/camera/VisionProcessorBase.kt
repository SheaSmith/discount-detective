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

package io.github.sheasmith.discountdetective.barcode.camera

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.GuardedBy
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.gms.tasks.Tasks
import com.google.android.odml.image.MediaMlImageBuilder
import com.google.android.odml.image.MlImage
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(VisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
abstract class VisionProcessorBase<T>(context: Context) {

    companion object {
        private const val TAG = "VisionProcessorBase"
    }

    private var activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val fpsTimer = Timer()
    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    // Whether this processor is already shut down
    private var isShutdown = false

    // Used to calculate latency, running in the same thread, no sync needed.
    private var numRuns = 0
    private var totalFrameMs = 0L
    private var maxFrameMs = 0L
    private var minFrameMs = Long.MAX_VALUE
    private var totalDetectorMs = 0L
    private var maxDetectorMs = 0L
    private var minDetectorMs = Long.MAX_VALUE

    // Frame count that have been processed so far in an one second interval to calculate FPS.
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 0

    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private var latestImage: ByteBuffer? = null

    @GuardedBy("this")
    private var latestImageMetaData: FrameMetadata? = null

    // To keep the images and metadata in process.
    @GuardedBy("this")
    private var processingImage: ByteBuffer? = null

    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null

    init {
        fpsTimer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    framesPerSecond = frameProcessedInOneSecondInterval
                    frameProcessedInOneSecondInterval = 0
                }
            },
            0,
            1000
        )
    }

    /**
     * Process an image frame from CameraX
     *
     * @param image The image to process.
     * @param graphicOverlay The graphic overlay to apply to.
     */
    @ExperimentalGetImage
    fun processImageProxy(image: ImageProxy, graphicOverlay: GraphicOverlay) {
        val frameStartMs = SystemClock.elapsedRealtime()
        if (isShutdown) {
            return
        }
        val bitmap: Bitmap? = null

        if (isMlImageEnabled(graphicOverlay.context)) {
            val mlImage =
                MediaMlImageBuilder(
                    image.image ?: return
                ).setRotation(image.imageInfo.rotationDegrees)
                    .build()
            requestDetectInImage(
                mlImage,
                graphicOverlay,
                /* originalCameraImage= */ bitmap,
                /* shouldShowFps= */ frameStartMs
            )
                // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                // Currently MlImage doesn't support ImageProxy directly, so we still need to call
                // ImageProxy.close() here.
                .addOnCompleteListener { image.close() }

            return
        }

        requestDetectInImage(
            InputImage.fromMediaImage(image.image ?: return, image.imageInfo.rotationDegrees),
            graphicOverlay,
            /* originalCameraImage= */ bitmap,
            /* shouldShowFps= */ frameStartMs
        )
            // When the image is from CameraX analysis use case, must call image.close() on received
            // images when finished using them. Otherwise, new images may not be received or the camera
            // may stall.
            .addOnCompleteListener { image.close() }
    }

    // -----------------Common processing logic-------------------------------------------------------
    private fun requestDetectInImage(
        image: InputImage,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?,
        frameStartMs: Long
    ): Task<T> {
        return setUpListener(
            detectInImage(image),
            graphicOverlay,
            originalCameraImage,
            frameStartMs
        )
    }

    private fun requestDetectInImage(
        image: MlImage,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?,
        frameStartMs: Long
    ): Task<T> {
        return setUpListener(
            detectInImage(image),
            graphicOverlay,
            originalCameraImage,
            frameStartMs
        )
    }

    private fun setUpListener(
        task: Task<T>,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?,
        frameStartMs: Long
    ): Task<T> {
        val detectorStartMs = SystemClock.elapsedRealtime()
        return task
            .addOnSuccessListener(
                executor
            ) { results: T ->
                val endMs = SystemClock.elapsedRealtime()
                val currentFrameLatencyMs = endMs - frameStartMs
                val currentDetectorLatencyMs = endMs - detectorStartMs
                if (numRuns >= 500) {
                    resetLatencyStats()
                }
                numRuns++
                frameProcessedInOneSecondInterval++
                totalFrameMs += currentFrameLatencyMs
                maxFrameMs = max(currentFrameLatencyMs, maxFrameMs)
                minFrameMs = min(currentFrameLatencyMs, minFrameMs)
                totalDetectorMs += currentDetectorLatencyMs
                maxDetectorMs = max(currentDetectorLatencyMs, maxDetectorMs)
                minDetectorMs = min(currentDetectorLatencyMs, minDetectorMs)

                // Only log inference info once per second. When frameProcessedInOneSecondInterval is
                // equal to 1, it means this is the first frame processed during the current second.
                if (frameProcessedInOneSecondInterval == 1) {
                    Log.d(TAG, "Num of Runs: $numRuns")
                    Log.d(
                        TAG,
                        "Frame latency: max=" +
                                maxFrameMs +
                                ", min=" +
                                minFrameMs +
                                ", avg=" +
                                totalFrameMs / numRuns
                    )
                    Log.d(
                        TAG,
                        "Detector latency: max=" +
                                maxDetectorMs +
                                ", min=" +
                                minDetectorMs +
                                ", avg=" +
                                totalDetectorMs / numRuns
                    )
                    val mi = ActivityManager.MemoryInfo()
                    activityManager.getMemoryInfo(mi)
                    val availableMegs: Long = mi.availMem / 0x100000L
                    Log.d(TAG, "Memory available in system: $availableMegs MB")
                }
                graphicOverlay.clear()
                if (originalCameraImage != null) {
                    graphicOverlay.add(CameraImageGraphic(graphicOverlay, originalCameraImage))
                }
                this@VisionProcessorBase.onSuccess(results, graphicOverlay)
                graphicOverlay.postInvalidate()
            }
            .addOnFailureListener(
                executor
            ) { e: Exception ->
                graphicOverlay.clear()
                graphicOverlay.postInvalidate()
                val error = "Failed to process. Error: " + e.localizedMessage
                Toast.makeText(
                    graphicOverlay.context,
                    """
          $error
          Cause: ${e.cause}
          """.trimIndent(),
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d(TAG, error)
                e.printStackTrace()
                this@VisionProcessorBase.onFailure(e)
            }
    }

    /**
     * Stop processing
     */
    open fun stop() {
        executor.shutdown()
        isShutdown = true
        resetLatencyStats()
        fpsTimer.cancel()
    }

    private fun resetLatencyStats() {
        numRuns = 0
        totalFrameMs = 0
        maxFrameMs = 0
        minFrameMs = Long.MAX_VALUE
        totalDetectorMs = 0
        maxDetectorMs = 0
        minDetectorMs = Long.MAX_VALUE
    }

    /**
     * Detect a vision item inside an image.
     *
     * @param image The image to detect.
     * @return A task that will contain the returned object.
     */
    protected abstract fun detectInImage(image: InputImage): Task<T>

    /**
     * Detect a vision item inside an image, but specifically for the ML based models.
     *
     * @param image The image to detect.
     * @return A task that will contain the returned object.
     */
    protected open fun detectInImage(image: MlImage): Task<T> {
        return Tasks.forException(
            MlKitException(
                "MlImage is currently not demonstrated for this feature",
                MlKitException.INVALID_ARGUMENT
            )
        )
    }

    /**
     * An object has been successfully detected.
     *
     * @param results The results from the detection.
     * @param graphicOverlay The graphic overlay for this view.
     */
    protected abstract fun onSuccess(results: T, graphicOverlay: GraphicOverlay)

    /**
     * There was a failure detecting an object.
     *
     * @param e The exception that caused the failure.
     */
    protected abstract fun onFailure(e: Exception)

    /**
     * Whether the image is an ML image or not.
     */
    protected open fun isMlImageEnabled(context: Context?): Boolean = false
}