package com.example.naturediary

class ListFile(val userId: String, val title: String, val location: String, val fileName: String) {

    override fun toString(): String {
        return "$userId, $title, $location, $fileName"
    }
}