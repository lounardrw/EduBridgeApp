package com.example.edubridge.ui // O donde prefieras

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.EduBridgeApp
import com.example.edubridge.data.ResourceRepository
import com.example.edubridge.data.model.Resource
import com.example.edubridge.network.ApiService
import com.example.edubridge.network.ResourceViewRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception


class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ResourceRepository

    // ESTADO PRINCIPAL
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Inicialización de Room para Recursos
        val resourceDao = (application as EduBridgeApp).database.resourceDao()
        repository = ResourceRepository(resourceDao)

        // Carga de datos desde Room y mapeo a la UI
        viewModelScope.launch {
            repository.getAllResources()
                .map { entities ->
                    entities.map { entity ->
                        Resource(
                            id = entity.id.toString(),
                            title = entity.contenido.title,
                            author = entity.author,
                            url = entity.contenido.url ?: ""
                        )
                    }
                }.collect { resources ->
                    // Cuando Room emite datos, actualizamos el estado de la UI
                    _uiState.update { it.copy(resources = resources, filteredResources = resources) }
                }
        }
    }

    //LÓGICA DE BÚSQUEDA
    fun onSearchQueryChanged(query: String) {
        val currentResources = _uiState.value.resources
        val filtered = if (query.isBlank()) {
            currentResources
        } else {
            currentResources.filter { resource ->
                resource.title.contains(query, ignoreCase = true) || resource.author.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(searchQuery = query, filteredResources = filtered) }
    }

    // LÓGICA DE GESTIÓN (PARA EL PROFESOR)
    fun addResource(title: String, author: String, url: String) {
        viewModelScope.launch {
            repository.insertResource(title, author, url) // Guardar en Room
        }
    }

    /**
     * REGISTRO DML: Notifica a MySQL que un alumno ha visualizado un recurso.
     * @param resourceId El ID de la Entidad de Room (String).
     * @param userMatricula La matrícula del alumno logueado.
     */
    fun logResourceView(resourceId: String, userMatricula: String) {
        viewModelScope.launch {
            try {
                // El ID de Room es INT, lo convertimos antes de enviar al DTO
                val idInt = resourceId.toIntOrNull() ?: 0

                if (idInt > 0) {
                    val request = ResourceViewRequest(
                        resource_id = idInt,
                        user_matricula = userMatricula
                    )

                    // Llamada al endpoint DML (biblioteca/recurso_visto.php)
                    val response = ApiService.api.logResourceView(request)

                    if (response.isSuccessful && response.body()?.ok == true) {
                        Log.i("LibraryDML", "Vista de recurso ID $resourceId registrada en MySQL.")
                    } else {
                        Log.e("LibraryDML", "Fallo al registrar vista: ${response.body()?.error} (Code: ${response.code()})")
                    }
                } else {
                    Log.w("LibraryDML", "ID de recurso inválido ($resourceId). No se envió DML.")
                }
            } catch (e: IOException) {
                Log.e("LibraryDML", "Error de red al registrar vista: ${e.message}")
            } catch (e: Exception) {
                Log.e("LibraryDML", "Excepción al registrar vista: ${e.message}")
            }
        }
    }
}

// CLASE DE ESTADO PARA LA UI
data class LibraryUiState(
    val searchQuery: String = "",
    val resources: List<Resource> = emptyList(),
    val filteredResources: List<Resource> = emptyList()
)