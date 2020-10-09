package com.example.naturediary

import android.net.Uri
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.list.view.*
import java.io.File

class FirebaseClass {
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
        val timestamp = System.currentTimeMillis()
        val data = hashMapOf(
            "createdAt" to timestamp,
            "userId" to userId,
            "title" to title,
            "location" to location,
            "fileName" to fileName
        )
        try {
            db.collection(userId).document(timestamp.toString()).set(data)
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
        val localFile = File.createTempFile("temp", ".pcm")
        val downloadTask = ref.getFile(localFile)
        downloadTask.addOnSuccessListener {
            Recorder.currentFile = localFile
            Recorder.fileName.value = localFile.name
            Recorder().play()
        }.addOnFailureListener {
            Log.d(TAG, "vittu")
        }
    }

    fun getLastItem(view: View) {
        db.collection(MainActivity.deviceId).orderBy("createdAt")
            .limitToLast(1).get()
            .addOnSuccessListener { document ->
                if (document.documents != null) {
                    for (i in document) {
                        view.tvTitle.text = i.data["title"].toString()
                        view.tvSubTitle.text =
                            i.data["location"].toString()
                        view.btnPlayFromList.setOnClickListener {
                            FirebaseClass().downloadRecording(
                                MainActivity.deviceId,
                                i.data["fileName"].toString()
                            )
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun updateList() {
        ListFiles.files.clear()
        db.collection(MainActivity.deviceId).orderBy("createdAt", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snap ->
                if (snap != null) {
                    for (i in snap) {
                        ListFiles.files.add(
                            ListFile(
                                i.data["createdAt"].toString(),
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

    fun deleteFile(createdAt: String) {
        db.collection(MainActivity.deviceId).document(createdAt).delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                updateList()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

}