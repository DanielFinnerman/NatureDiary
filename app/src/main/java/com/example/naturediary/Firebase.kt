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

    fun upload(userId: String, title: String, location: String, fileName: String) {
        val data = hashMapOf(
            "userId" to userId,
            "title" to title,
            "location" to location,
            "fileName" to fileName
        )
        try {
            db.collection(userId).add(data)
        } catch (e: Error) {
            Log.d(TAG, "upload error: $e")
        }
    }

    fun uploadRecording(userId: String, file: File) {
        val ref = storage.reference.child("$userId/${file.name}")
        val uploadTask = ref.putFile(Uri.fromFile(file))
        uploadTask.addOnSuccessListener {
            Log.d(TAG, "filu up")
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

    fun updateList() {
        ListFiles.files.clear()
        db.collection(MainActivity.deviceId).get()
            .addOnSuccessListener { snap ->
                if (snap != null) {
                    for (i in snap) {
                        ListFiles.files.add(
                            ListFile(
                                i.data["userId"].toString(),
                                i.data["title"].toString(),
                                i.data["location"].toString(),
                                i.data["fileName"].toString()
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