package com.example.naturediary

class ListFile(val id: String, val name: String, val location: String) {
    override fun toString(): String {
        return "$id, $name.take, $location"
    }
}