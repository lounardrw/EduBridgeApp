package com.example.edubridge.data.local.entitymodel

import androidx.room.ColumnInfo

/**
 * SQLITE: Clase de datos embebida (Composición) para campos comunes.
 * Se usa @Embedded en Entidades (Event, Resource) para simular la herencia.
 */
data class Contenido(
    @ColumnInfo(name = "titulo")
    val title: String,

    @ColumnInfo(name = "descripcion")
    val description: String,

    // Campo para la URL (común en Recursos y potencialmente Quizzes/Eventos)
    @ColumnInfo(name = "url")
    val url: String? = null
)