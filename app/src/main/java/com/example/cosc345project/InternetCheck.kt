package com.example.cosc345project

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import com.example.cosc345project.exceptions.NoInternetException

/**
 * Check whether the internet is connected or not.
 *
 * @param context The context to use to get the connectivity manager.
 * @throws NoInternetException An exception thrown when there is no internet.
 */
fun checkInternet(context: Context) {
    val connectivityManager = getSystemService(context, ConnectivityManager::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager?.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        if (capabilities == null || !(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ))
        ) {
            throw NoInternetException()
        }
    } else {
        // We are required to use deprecated methods, as the officially supported methods were only added in Android M (API Level 23)
        @Suppress("DEPRECATION") val networkInfo = connectivityManager?.activeNetworkInfo

        @Suppress("DEPRECATION") if (networkInfo == null || !networkInfo.isConnected) {
            throw NoInternetException()
        }
    }
}