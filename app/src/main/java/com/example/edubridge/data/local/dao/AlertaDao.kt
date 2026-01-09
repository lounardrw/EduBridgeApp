package com.example.edubridge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.edubridge.data.local.entitymodel.AlertaUbicacion
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la entidad AlertaUbicacion.
 */
@Dao
interface AlertaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alerta: AlertaUbicacion)

    @Query("SELECT * FROM alerta_ubicacion ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertaUbicacion>>

    @Query("DELETE FROM alerta_ubicacion")
    suspend fun deleteAll()

    // Método para obtener las alertas no enviadas (para reintento)
    @Query("SELECT * FROM alerta_ubicacion LIMIT 10")
    suspend fun getPendingAlerts(): List<AlertaUbicacion>
}