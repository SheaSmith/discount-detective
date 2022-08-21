package com.example.cosc345project.repository

import com.example.cosc345.shared.models.Retailer
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retailers Repository Class.
 *
 * Accesses the information about retailers from where it is stored in our Firebase Database.
 */
@Singleton
class RetailersRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getRetailers(): Map<String, Retailer> {
        return suspendCancellableCoroutine { continuation ->
            database.reference.child("retailers").get().addOnSuccessListener { dataSnapshot ->
                continuation.resume(dataSnapshot.getValue<Map<String, Retailer>>()!!, null)
            }
        }
    }
}