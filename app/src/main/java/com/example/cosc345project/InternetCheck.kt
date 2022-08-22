package com.example.cosc345project

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    val network = connectivityManager?.activeNetwork
    val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

    if (capabilities == null || !(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        ))
    ) {
        throw NoInternetException()
    }
}