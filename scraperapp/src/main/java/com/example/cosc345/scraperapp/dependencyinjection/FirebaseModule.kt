package com.example.cosc345.scraperapp.dependencyinjection

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides several Firebase dependencies for dependency injection.
 */
// https://stackoverflow.com/questions/67542337/android-inject-firebase-firestore-with-hilt-in-repository-pattern
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    /**
     * Provide Firebase Authentication for injection.
     *
     * @return An instance of [FirebaseAuth] for making authentication operations.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthentication(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /**
     * Provide Firebase Database for injection.
     *
     * @return An instance of [FirebaseDatabase] pointing to our specific database.
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database("https://discount-detective-default-rtdb.asia-southeast1.firebasedatabase.app/")
    }
}