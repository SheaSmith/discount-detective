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

/**
 * Describing the metadata of a frame.
 *
 * @param width The width you wish for the frame to be.
 * @param height The height you wish the frame to be.
 * @param rotation Any rotation the frame should have.
 */
class FrameMetadata private constructor(val width: Int, val height: Int, val rotation: Int) {

    /** Builder of [FrameMetadata].  */
    class Builder {
        private var width = 0
        private var height = 0
        private var rotation = 0

        /**
         * Build the frame metadata.
         */
        fun build(): FrameMetadata {
            return FrameMetadata(width, height, rotation)
        }
    }
}