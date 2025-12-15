package com.example.edubridge.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.data.remote.RetrofitClient
import com.example.edubridge.data.remote.events.EventRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherEventsViewModel : ViewModel() {

    private val api = RetrofitClient.api

    private val _events = MutableStateFlow<List<EventData>>(emptyList())
    val events: StateFlow<List<EventData>> = _events.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.getEventos()
                if (response.ok) {
                    _events.value = response.data?.map { it.toEventData() } ?: emptyList()
                } else {
                    _error.value = response.error ?: "Error al cargar eventos"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun addEvent(event: EventData, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val request = EventRequest(
                    title = event.title,
                    description = event.description,
                    date = event.date,
                    created_by = 1 // id del profesor logueado
                )
                val response = api.createEvent(request)
                if (response.isSuccessful && response.body()?.ok == true) {
                    loadEvents()
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

    fun updateEvent(event: EventData, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val request = EventRequest(
                    id = event.id,
                    title = event.title,
                    description = event.description,
                    date = event.date,
                    created_by = 1
                )
                val response = api.updateEvent(request)
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
                api.deleteEvent(mapOf("id" to eventId))
                loadEvents() // recarga la lista
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

}
