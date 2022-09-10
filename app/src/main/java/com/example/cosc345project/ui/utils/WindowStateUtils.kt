/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cosc345project.ui.utils

import android.graphics.Rect
import androidx.window.layout.FoldingFeature
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Information about the posture of the device, for supporting folding devices.
 */
sealed interface DevicePosture {
    /**
     * A normal phone posture (e.g. folded for a folding device).
     */
    object NormalPosture : DevicePosture

    /**
     * Open, like a book for a folded device.
     */
    data class BookPosture(
        /**
         * The current position of the hinge.
         */
        val hingePosition: Rect
    ) : DevicePosture

    /**
     * In the process of opening for a folden device.
     */
    data class Separating(
        /**
         * The current position of the hinge.
         */
        val hingePosition: Rect,

        /**
         * The orientation of the device.
         */
        var orientation: FoldingFeature.Orientation
    ) : DevicePosture
}

/**
 * Is the folding device in a "book" posture.
 */
@OptIn(ExperimentalContracts::class)
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

/**
 * If the folding device is in the process of separating.
 */
@OptIn(ExperimentalContracts::class)
fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}

/**
 * Different type of navigation supported by app depending on size and state.
 */
enum class NavigationType {
    /**
     * The app should use a bottom navigation bar.
     */
    BOTTOM_NAVIGATION,

    /**
     * The app should use a navigation rail on the left, with a modal drawer.
     */
    NAVIGATION_RAIL,

    /**
     * The app should use a permanent navigation drawer.
     */
    PERMANENT_NAVIGATION_DRAWER
}