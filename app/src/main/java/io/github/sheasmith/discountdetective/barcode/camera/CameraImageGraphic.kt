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

import android.graphics.Bitmap
import android.graphics.Canvas
import io.github.sheasmith.discountdetective.barcode.camera.GraphicOverlay.Graphic

/**
 * An overlay for the camera that shows a static image.
 *
 * @param overlay The graphic overlay to use for the overlay.
 * @param bitmap The bitmap to overlay.
 */
class CameraImageGraphic(overlay: GraphicOverlay?, private val bitmap: Bitmap) : Graphic(
    overlay!!
) {
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, getTransformationMatrix(), null)
    }
}