package com.example.edubridge.ui // O donde prefieras

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LibraryViewModel : ViewModel() {

    // --- ESTADO PRINCIPAL ---
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    // --- DATOS DE EJEMPLO (en un futuro vendrán de Firebase) ---
    private val allResources = listOf(
        Resource("1", "Guía de Álgebra Lineal", "Dr. A. Valdor", "https://www.google.com/search?q=algebra+lineal+pdf"),
        Resource("2", "Principios de Cálculo", "M. Spivak", "https://www.google.com/search?q=calculo+pdf"),
        Resource("3", "Física para Universitarios", "Sears y Zemansky", "https://www.google.com/search?q=fisica+universitaria+pdf"),
        Resource("4", "Introducción a la Programación", "Joyanes Aguilar", "https://www.google.com/search?q=programacion+pdf")
    )

    init {
        // Carga inicial de datos
        _uiState.value = LibraryUiState(resources = allResources, filteredResources = allResources)
    }

    // --- LÓGICA DE BÚSQUEDA ---
    fun onSearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            allResources
        } else {
            allResources.filter {
                it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(searchQuery = query, filteredResources = filtered) }
    }

    // --- LÓGICA DE GESTIÓN (PARA EL PROFESOR) ---
    fun addResource(title: String, author: String, url: String) {
        // TODO: Lógica para añadir recurso (futuro Firebase)
        println("Añadiendo: $title, $author, $url")
    }
}

// --- CLASE DE ESTADO PARA LA UI ---
data class LibraryUiState(
    val searchQuery: String = "",
    val resources: List<Resource> = emptyList(),
    val filteredResources: List<Resource> = emptyList()
)
