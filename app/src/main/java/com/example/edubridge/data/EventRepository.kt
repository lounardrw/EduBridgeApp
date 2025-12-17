package com.example.edubridge.data

import com.example.edubridge.data.local.dao.EventDao
import com.example.edubridge.data.local.entitymodel.Event
import com.example.edubridge.data.local.entitymodel.Contenido
import com.example.edubridge.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {
    // 1. Flujo para leer siempre desde la caché de Room
    fun getLocalEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents()
    }

    // 2. Lógica para obtener de la red y cachear
    suspend fun refreshEvents() = withContext(Dispatchers.IO) {
        try {
            val response = apiService.api.getEvents() // Llamada a la API
            if (response.isSuccessful && response.body() != null) {
                val remoteEvents = response.body()!!

                // Mapear DTO de Red a Entidad de Room (Event)
                val eventEntities = remoteEvents.map { dto ->
                    Event(
                        id = dto.id ?: 0,
                        contenido = Contenido(
                            title = dto.title,
                            description = dto.description ?: "Sin descripción",
                            url = null //
                        ),
                        date = dto.date,
                        type = "AVISOS"
                    )
                }

                eventDao.deleteAllEvents()
                eventEntities.forEach { eventDao.insert(it) }

            } else {
                throw IOException("Error al obtener eventos: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Consumo de los datos de la caché
            throw e
        }
    }
}
