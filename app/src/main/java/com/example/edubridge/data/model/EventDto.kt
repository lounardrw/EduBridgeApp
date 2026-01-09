package com.example.edubridge.data.model

/**
 * DTO para recibir la información de eventos desde MySQL.
 * Se agregaron 'type' y 'url' para que el alumno pueda filtrar por categorías
 * y ver imágenes/enlaces.
 */
data class EventDto(
    val id: Int?,
    val title: String?,
    val description: String?,
    val date: String?,
    val created_by: Int?,
    val creador: String?,   // Viene del JOIN en PHP
    val created_at: String?,

    // CAMPOS NUEVOS PARA RESOLVER EL FILTRADO Y LAS IMÁGENES
    val type: String? = "AVISOS", // Punto 2: Categoría (BECAS, PROXIMOS, AVISOS)
    val url: String? = null       // Punto 2: URL de la imagen o link
)