package com.example.naturediary

class ListFile(val createdAt: String, val userId: String, val title: String, val location: String, val fileName: String) {

    override fun toString(): String {
        return "$createdAt, $userId, $title, $location, $fileName"
    }
}