package com.example.cosc345.scraperapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * The view model for the main screen.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    /**
     * Whether the login should be shown or not.
     */
    var showLogin: Boolean by mutableStateOf(firebaseAuth.currentUser == null)

    /**
     * Handle a login to Firebase.
     *
     * @param result The result from Firebase.
     */
    fun handleLogin(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = firebaseAuth.currentUser
            showLogin = user == null
        }
    }

    /**
     * Launch the login if requested.
     *
     * @param signInLauncher The activity launcher for the login screen.
     */
    fun launchLogin(signInLauncher: ActivityResultLauncher<Intent>) {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }
}