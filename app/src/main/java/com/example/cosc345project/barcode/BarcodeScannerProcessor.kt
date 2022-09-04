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

package com.example.cosc345project.barcode

import android.content.Context
import android.util.Log
import com.example.cosc345project.barcode.camera.GraphicOverlay
import com.example.cosc345project.barcode.camera.VisionProcessorBase
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/** Barcode Detector Demo.  */
class BarcodeScannerProcessor(context: Context) : VisionProcessorBase<List<Barcode>>(context) {

    // Note that if you know which format of barcode your app is dealing with, detection will be
    // faster to specify the supported barcode formats one by one, e.g.
    // BarcodeScannerOptions.Builder()
    //     .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
    //     .build();
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

    override fun stop() {
        super.stop()
        barcodeScanner.close()
    }

    override fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return barcodeScanner.process(image)
    }

    override fun onSuccess(results: List<Barcode>, graphicOverlay: GraphicOverlay) {
        if (results.isEmpty()) {
            Log.v("BarcodeScannerProcessor", "No barcode has been detected")
        }
        for (i in results.indices) {
            val barcode = results[i]
            graphicOverlay.add(BarcodeGraphic(graphicOverlay, barcode))
            logExtrasForTesting(barcode)
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Barcode detection failed $e")
    }

    companion object {
        private const val TAG = "BarcodeProcessor"

        private fun logExtrasForTesting(barcode: Barcode?) {
            if (barcode != null) {
                Log.v(
                    "BarcodeScannerProcessor",
                    String.format(
                        "Detected barcode's bounding box: %s",
                        barcode.boundingBox!!.flattenToString()
                    )
                )
                Log.v(
                    "BarcodeScannerProcessor",
                    String.format(
                        "Expected corner point size is 4, get %d",
                        barcode.cornerPoints!!.size
                    )
                )
                for (point in barcode.cornerPoints!!) {
                    Log.v(
                        "BarcodeScannerProcessor",
                        String.format(
                            "Corner point is located at: x = %d, y = %d",
                            point.x,
                            point.y
                        )
                    )
                }
                Log.v(
                    "BarcodeScannerProcessor",
                    "barcode display value: " + barcode.displayValue
                )
                Log.v(
                    "BarcodeScannerProcessor",
                    "barcode raw value: " + barcode.rawValue
                )
            }
        }
    }
}