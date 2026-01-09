package com.example.edubridge.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.EduBridgeApp
import com.example.edubridge.data.ResourceRepository
import com.example.edubridge.data.local.entitymodel.ResourceEntity
import com.example.edubridge.data.local.entitymodel.Contenido
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Modelo de datos para la Interfaz (Alumno y Profesor).
 */
data class LibraryModuleUI(
    val id: String,
    val title: String,
    val author: String,
    val url: String
)

/**
 * Estado de la pantalla.
 */
data class LibraryUiState(
    val searchQuery: String = "",
    val filteredResources: List<LibraryModuleUI> = emptyList()
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ResourceRepository
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    private var allResources: List<LibraryModuleUI> = emptyList()

    init {
        val dao = (application as EduBridgeApp).database.resourceDao()
        repository = ResourceRepository(dao)

        viewModelScope.launch {
            repository.getAllResources().collect { entities ->
                allResources = entities.map {
                    LibraryModuleUI(
                        id = it.id.toString(),
                        title = it.contenido.title,
                        author = it.author,
                        url = it.contenido.url ?: ""
                    )
                }
                applyFilter(_uiState.value.searchQuery)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter(query)
    }

    private fun applyFilter(query: String) {
        val filtered = if (query.isBlank()) allResources
        else allResources.filter { it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true) }
        _uiState.update { it.copy(filteredResources = filtered) }
    }

    fun saveResource(id: String?, title: String, author: String, url: String) {
        viewModelScope.launch {
            if (id == null) {
                repository.insertResource(title, author, url)
            } else {
                val entityId = id.toIntOrNull() ?: 0
                val updated = ResourceEntity(
                    id = entityId,
                    contenido = Contenido(title, "Recurso de biblioteca", url),
                    author = author
                )
                repository.updateResource(updated)
            }
        }
    }

    fun deleteResource(id: String) {
        viewModelScope.launch {
            id.toIntOrNull()?.let { repository.deleteResourceById(it) }
        }
    }
}