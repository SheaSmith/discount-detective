package io.github.sheasmith.discountdetective.exceptions

/**
 * The exception thrown when a user is offline and attempts to query an online resource.
 */
class NoInternetException : Exception() {
    companion object {
        // Kotlin seemed to want this?
        private const val serialVersionUID: Long = -3656423184556269134L
    }
}