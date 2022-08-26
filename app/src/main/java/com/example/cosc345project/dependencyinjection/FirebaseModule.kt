package com.example.cosc345project.dependencyinjection

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// https://stackoverflow.com/questions/67542337/android-inject-firebase-firestore-with-hilt-in-repository-pattern
/**
 * Provide relevant Firebase objects for injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    /**
     * Get an instance of our Firebase database.
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database("https://discount-detective-default-rtdb.asia-southeast1.firebasedatabase.app/")
    }
}