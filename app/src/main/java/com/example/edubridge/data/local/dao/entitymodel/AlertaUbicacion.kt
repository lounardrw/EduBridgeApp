package com.example.edubridge.data.local.entitymodel

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SQLITE: Entidad de Alerta.
 * Usada como FALLBACK para guardar la ubicación de pánico si la conexión a MySQL falla.
 */
@Entity(tableName = "alerta_ubicacion")
data class AlertaUbicacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)