package com.example.edubridge.data.model
// DTO para el cuerpo de la petición de Login (Usado por PHP/MySQL)
data class LoginRequest(
    val email: String,
    val password: String
)