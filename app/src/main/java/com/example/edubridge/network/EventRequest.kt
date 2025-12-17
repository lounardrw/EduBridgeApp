package com.example.edubridge.network

data class EventRequest(
    val id: Int? = null,
    val title: String,
    val description: String?,
    val date: String,
    val created_by: Int
)