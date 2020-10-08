package com.example.naturediary

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class Firebase {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val storage = Firebase.storage

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

    fun uploadRecording(userId: String, file: File) {
        val ref = storage.reference.child("$userId/${file.name}")
        val uploadTask = ref.putFile(Uri.fromFile(file))
        uploadTask.addOnSuccessListener {

        }.addOnFailureListener {
            Log.d(TAG, "vittu")
        }

    }

    fun downloadRecording(userId: String, fileName: String) {
        val ref = storage.reference.child("$userId/${fileName}")
        val localFile = File.createTempFile("record", "pcm")
        val downloadTask = ref.getFile(localFile)
        downloadTask.addOnSuccessListener {
            Recorder.currentFile = localFile
            Recorder().play()
        }.addOnFailureListener {
            Log.d(TAG, "vittu")
        }
    }

    fun findById(id: String) {
        db.collection("ville").document(id).get()
            .addOnSuccessListener { snap ->
                if (snap != null) {
                    Log.d(TAG, "${snap.data}")
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
                        ListFiles.files.add(
                            ListFile(
                                i.data["id"].toString(),
                                i.data["name"].toString(),
                                i.data["location"].toString()
                            )
                        )
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