package com.example.naturediary

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Firebase {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    fun authenticate() {
        auth = FirebaseAuth.getInstance()
        try {
            auth.signInAnonymously()
            Log.d(TAG, "logged in")
        } catch (e: Error) {
            Log.d(TAG, "authenticate error: $e")
        }
    }

    fun upload(id: String, name: String, location: String) {
        val data = hashMapOf(
            "id" to id,
            "name" to name,
            "location" to location
        )
        try {
            db.collection(id).add(data)
        } catch (e: Error) {
            Log.d(TAG, "upload error: $e")
        }
    }

    fun findById(id: String) {
        db.collection("ville").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun findAllById(id: String) {
        db.collection(id).get()
            .addOnSuccessListener { snap ->
                if (snap != null) {
                    for (i in snap) {
                        Log.d(TAG, "${i.data["name"]}")
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

}