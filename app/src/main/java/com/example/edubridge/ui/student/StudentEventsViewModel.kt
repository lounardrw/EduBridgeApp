package com.example.edubridge.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentEventsViewModel : ViewModel() {

    private val api = RetrofitClient.api

    private val _events = MutableStateFlow<List<EventData>>(emptyList())
    val events: StateFlow<List<EventData>> = _events.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadEvents() {
        viewModelScope.launch {
            try {
                _loading.value = true

                val response = api.getEventos()

                if (response.ok) {
                    _events.value =
                        response.data
                            ?.map { it.toEventData() }
                            ?: emptyList()
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
}
