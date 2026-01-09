package com.example.edubridge.data

import com.example.edubridge.data.local.dao.EventDao
import com.example.edubridge.data.local.entitymodel.Event
import com.example.edubridge.data.local.entitymodel.Contenido
import com.example.edubridge.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {
    fun getLocalEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    suspend fun refreshEvents() = withContext(Dispatchers.IO) {
        try {
            val response = apiService.api.getEvents()
            if (response.isSuccessful && response.body() != null) {
                val remoteEvents = response.body()!!

                val eventEntities = remoteEvents.map { dto ->
                    Event(
                        id = dto.id ?: 0,
                        contenido = Contenido(
                            title = dto.title ?: "Sin título",
                            description = dto.description ?: "",
                            url = dto.url // <--- Traemos la imagen de MySQL
                        ),
                        date = dto.date ?: "2025-01-01",
                        // <--- RESPETAMOS LA CATEGORÍA DE MYSQL
                        type = dto.type ?: "AVISOS"
                    )
                }

                eventDao.deleteAllEvents()
                eventEntities.forEach { eventDao.insert(it) }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}