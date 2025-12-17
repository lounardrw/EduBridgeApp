package com.example.edubridge.data.model

// DTO para manejar los recursos en la UI y el ViewModel de la Biblioteca.
data class Resource(
    val id: String,
    val title: String,
    val author: String,
    val url: String
)