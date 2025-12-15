package com.example.edubridge.data.model


data class EventDto(
    val id: Int,
    val title: String,
    val description: String?,
    val date: String,
    val created_by: Int,
    val creador: String?,   // viene del JOIN
    val created_at: String
)
