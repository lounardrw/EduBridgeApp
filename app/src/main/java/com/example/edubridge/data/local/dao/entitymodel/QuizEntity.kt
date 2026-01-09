package com.example.edubridge.data.local.entitymodel

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad principal del Quiz
@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    val contenido: QuizContenido,
    val gradeLevel: String,
    val status: String,
    val formUrl: String
)

// Clase @Embedded para datos anidados
data class QuizContenido(
    val title: String,
    val description: String
)