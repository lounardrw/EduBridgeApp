package com.example.edubridge.ui.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.EduBridgeApp
import com.example.edubridge.data.EventRepository
import com.example.edubridge.data.local.entitymodel.Event
import com.example.edubridge.network.ApiService
import com.example.edubridge.network.GenericResponse
import com.example.edubridge.network.EventRequest // Importación corregida
import com.example.edubridge.ui.student.EventModuleUI
import com.example.edubridge.ui.student.EventType
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class TeacherEventsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository

    private val _events = MutableStateFlow<List<EventModuleUI>>(emptyList())
    val events: StateFlow<List<EventModuleUI>> = _events.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val appDatabase = (application as EduBridgeApp).database
        val eventDao = appDatabase.eventDao()

        repository = EventRepository(ApiService, eventDao)

        loadEvents()
    }

    private fun Event.toUiModel(): EventModuleUI {
        val type = if (this.contenido.title.contains("beca", ignoreCase = true)) EventType.BECAS else EventType.AVISOS

        return EventModuleUI(
            id = this.id,
            title = this.contenido.title,
            description = this.contenido.description,
            longDescription = this.contenido.description,
            date = this.date,
            type = type,
            categoryColor = if (type == EventType.BECAS) Color(0xFF0D47A1) else Color(0xFF169600)
        )
    }

    fun loadEvents() {
        viewModelScope.launch {
            _loading.value = true
            try {
                // 1. Forzar actualización de la caché desde la red
                repository.refreshEvents()

                // 2. Leer la caché actualizada (Room) y emitirla a la UI
                repository.getLocalEvents()
                    .map { entities ->
                        entities.map { entity ->
                            entity.toUiModel()
                        }
                    }
                    .collect { mappedEvents ->
                        _events.value = mappedEvents
                        _loading.value = false
                    }
            } catch (e: IOException) {
                // Error de red. Leer caché para no dejar la pantalla en blanco
                repository.getLocalEvents()
                    .map { entities ->
                        entities.map { it.toUiModel() }
                    }
                    .collect { mappedEvents ->
                        _events.value = mappedEvents
                    }
                _loading.value = false
                _error.value = "Error de red al cargar eventos. Mostrando caché."
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message
            }
        }
    }

    // --- FUNCIONES CRUD DEL PROFESOR ---

    fun addEvent(event: EventModuleUI, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val request = EventRequest(
                    id = null, // Para INSERT
                    title = event.title,
                    description = event.description,
                    date = event.date,
                    created_by = 1
                )
                val response = ApiService.api.createEvent(request)

                if (response.isSuccessful && response.body()?.ok == true) {
                    loadEvents() // Recarga la lista desde la red para que se refleje
                    onSuccess()
                } else {
                    _error.value = response.body()?.error ?: "Error al crear evento"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateEvent(event: EventModuleUI, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val request = EventRequest(
                    id = event.id, // Para UPDATE
                    title = event.title,
                    description = event.description,
                    date = event.date,
                    created_by = 1
                )
                val response = ApiService.api.updateEvent(request)
                if (response.isSuccessful && response.body()?.ok == true) {
                    loadEvents()
                    onSuccess()
                } else {
                    _error.value = response.body()?.error ?: "Error al actualizar evento"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteEvent(eventId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = ApiService.api.deleteEvent(mapOf("id" to eventId))

                if (response.isSuccessful) {
                    loadEvents()
                    onSuccess()
                } else {
                    _error.value = "Error al eliminar evento: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
