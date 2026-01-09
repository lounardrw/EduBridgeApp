package com.example.edubridge.data.model
// DTO para la respuesta de inicio de sesión del servidor.
data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val userRole: String?,
    val id: String? = null
)