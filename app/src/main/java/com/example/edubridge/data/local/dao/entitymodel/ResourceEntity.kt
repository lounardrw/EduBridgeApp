package com.example.edubridge.data.local.entitymodel

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTIDAD: ResourceEntity
 * Hereda de Contenido.
 */
@Entity(tableName = "recursos")
data class ResourceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @Embedded // Aplicamos Herencia
    val contenido: Contenido,

    val author: String
)