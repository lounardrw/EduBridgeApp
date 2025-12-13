package com.example.edubridge.data.model

data class UserResponse(
    val ok: Boolean,
    val id: String? = null,
    val matricula: String? = null,
    val nombre: String? = null,
    val correo: String? = null,
    val rol: String? = null,
    val error: String? = null
)
