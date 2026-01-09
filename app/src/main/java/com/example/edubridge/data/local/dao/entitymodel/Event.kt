package com.example.edubridge.data.local.entitymodel

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTIDAD: Event
 * Hereda de Contenido.
 */
@Entity(tableName = "eventos")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @Embedded // Aplicamos Herencia
    val contenido: Contenido,

    val date: String,
    val type: String
)