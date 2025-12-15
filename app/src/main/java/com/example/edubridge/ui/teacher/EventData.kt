package com.example.edubridge.ui.teacher

import androidx.compose.ui.graphics.Color
import com.example.edubridge.ui.student.EventType


data class EventData(
    val id: Int, // necesario para CRUD
    val title: String,
    val description: String,
    val longDescription: String,
    val date: String,
    val categoryColor: Color,
    val imageResId: Int,
    val type: EventType
)

