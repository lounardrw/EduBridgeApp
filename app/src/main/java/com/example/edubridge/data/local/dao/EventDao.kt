package com.example.edubridge.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.edubridge.data.local.entitymodel.Event
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la entidad Event (Eventos y Avisos).
 * Hereda del BaseDao.
 */
@Dao
interface EventDao : BaseDao<Event> {

    @Query("SELECT * FROM eventos ORDER BY date DESC")
    fun getAllEvents(): Flow<List<Event>>

    // Agregamos el método faltante para limpiar la caché de eventos.
    @Query("DELETE FROM eventos")
    suspend fun deleteAllEvents()
}
