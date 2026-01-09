package com.example.edubridge.network

/**
 * DTO para la comunicación con MySQL.
 * Se añade 'id' como opcional para soportar la función UPDATE.
 */
data class EventRequest(
    val id: Int? = null, // FIX: Ahora el constructor acepta ID
    val title: String,
    val description: String?,
    val date: String,
    val created_by: Int,
    val type: String,
    val url: String? = null
)