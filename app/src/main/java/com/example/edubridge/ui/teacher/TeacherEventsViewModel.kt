package com.example.edubridge.ui.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.EduBridgeApp
import com.example.edubridge.data.EventRepository
import com.example.edubridge.network.ApiService
import com.example.edubridge.network.EventRequest
import com.example.edubridge.ui.student.EventModuleUI
import com.example.edubridge.ui.student.EventType
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TeacherEventsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository
    private val _events = MutableStateFlow<List<EventModuleUI>>(emptyList())
    val events: StateFlow<List<EventModuleUI>> = _events.asStateFlow()

    // --- LÓGICA DE BÚSQUEDA ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Combina la lista de eventos con la búsqueda en tiempo real
    val filteredEvents = combine(_events, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val db = (application as EduBridgeApp).database
        repository = EventRepository(ApiService, db.eventDao())
        loadEvents()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                repository.refreshEvents()
                repository.getLocalEvents().collect { entities ->
                    _events.value = entities.map { entity ->
                        val eventType = when(entity.type.uppercase()) {
                            "BECAS" -> EventType.BECAS
                            "PRÓXIMOS", "PROXIMOS" -> EventType.PRÓXIMOS
                            else -> EventType.AVISOS
                        }
                        EventModuleUI(
                            id = entity.id,
                            title = entity.contenido.title,
                            description = entity.contenido.description,
                            longDescription = entity.contenido.description,
                            date = entity.date,
                            categoryColor = when(eventType) {
                                EventType.BECAS -> Color(0xFF0D47A1)
                                EventType.PRÓXIMOS -> Color(0xFFE65100)
                                else -> Color(0xFF169600)
                            },
                            type = eventType,
                            imageUrl = entity.contenido.url
                        )
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun addEvent(title: String, description: String, type: EventType, url: String) {
        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = sdf.format(Date())
                val request = EventRequest(
                    title = title,
                    description = description,
                    date = currentDate,
                    created_by = 1,
                    type = type.name,
                    url = url
                )
                val response = ApiService.api.createEvent(request)
                if (response.isSuccessful) loadEvents()
            } catch (e: Exception) { }
        }
    }

    fun updateEvent(id: Int, title: String, desc: String, type: EventType, url: String) {
        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = sdf.format(Date())
                val request = EventRequest(
                    id = id,
                    title = title,
                    description = desc,
                    date = currentDate,
                    created_by = 1,
                    type = type.name,
                    url = url
                )
                val response = ApiService.api.updateEvent(request)
                if (response.isSuccessful) loadEvents()
            } catch (e: Exception) { }
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiService.api.deleteEvent(mapOf("id" to eventId))
                if (response.isSuccessful) loadEvents()
            } catch (e: Exception) { }
        }
    }
}