package com.example.edubridge.ui.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import androidx.compose.ui.graphics.Color
import com.example.edubridge.EduBridgeApp
import com.example.edubridge.data.EventRepository
import com.example.edubridge.data.local.entitymodel.Event
import com.example.edubridge.network.ApiService

enum class EventType(val displayName: String) {
    TODO("Todo"),
    PRÓXIMOS("Próximos"),
    BECAS("Becas"),
    AVISOS("Avisos")
}

data class EventModuleUI(
    val id: Int,
    val title: String,
    val description: String,
    val longDescription: String,
    val date: String,
    val categoryColor: Color,
    val type: EventType
)

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository

    private val _uiState = MutableStateFlow(EventUiState(loading = true))
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    init {
        val appDatabase = (application as EduBridgeApp).database
        val eventDao = appDatabase.eventDao()

        repository = EventRepository(ApiService, eventDao)

        // Monitorear la base de datos local y mapear a EventModuleUI
        repository.getLocalEvents()
            .map { entities ->
                entities.map { it.toUiModel() }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            ).let { flow ->
                viewModelScope.launch {
                    flow.collect { events ->
                        _uiState.value = _uiState.value.copy(
                            events = events,
                            loading = false,
                            error = if (events.isEmpty() && !_uiState.value.isInitialLoad) "No hay eventos disponibles (offline)." else null
                        )
                    }
                }
            }

        loadEventsFromNetwork()
    }

    // Función de extensión para mapear Event de Room a EventModuleUI
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


    fun loadEventsFromNetwork() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, isInitialLoad = false)
            try {
                repository.refreshEvents()
                _uiState.value = _uiState.value.copy(loading = false, error = null)
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "Error de red. Mostrando datos de caché."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "Error general: ${e.message}"
                )
            }
        }
    }

    fun filterEvents(eventType: EventType) {
        _uiState.value = _uiState.value.copy(currentFilter = eventType)
    }
}

data class EventUiState(
    val events: List<EventModuleUI> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val currentFilter: EventType = EventType.TODO,
    val isInitialLoad: Boolean = true
)