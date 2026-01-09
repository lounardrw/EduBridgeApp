package com.example.edubridge.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.EduBridgeApp
import com.example.edubridge.data.QuizRepository
import com.example.edubridge.data.local.entitymodel.QuizContenido
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * DTO para la UI. Incluye descripción y URL para cumplir con el Punto 3.
 */
data class QuizModuleUI(
    val id: Int,
    val title: String,
    val description: String,
    val grade: String,
    val status: String,
    val formUrl: String
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val quizRepository: QuizRepository

    // Punto 4: Estado para la búsqueda avanzada
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        val quizDao = (application as EduBridgeApp).database.quizDao()
        quizRepository = QuizRepository(quizDao)
    }

    // Flujo base desde Room
    val quizzes: StateFlow<List<QuizModuleUI>> = quizRepository.getAllQuizzes()
        .map { list ->
            list.map { entity ->
                QuizModuleUI(
                    id = entity.id,
                    title = entity.contenido.title,
                    description = entity.contenido.description,
                    grade = entity.gradeLevel,
                    status = entity.status,
                    formUrl = entity.formUrl
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Punto 4: Lista filtrada dinámicamente para el profesor
    val filteredQuizzes = combine(quizzes, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter { it.title.contains(query, ignoreCase = true) || it.grade.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(newQuery: String) { _searchQuery.value = newQuery }

    // Punto 3: SOLUCIÓN AL ERROR 1, 4 - Modificación de contenido
    fun updateQuizContent(id: Int, title: String, description: String, grade: String, url: String) {
        viewModelScope.launch {
            val entity = quizRepository.getQuizById(id)
            entity?.let {
                val updated = it.copy(
                    contenido = QuizContenido(title, description),
                    gradeLevel = grade,
                    formUrl = url
                )
                quizRepository.updateQuiz(updated)
            }
        }
    }

    fun insertQuiz(title: String, description: String, grade: String, url: String) {
        viewModelScope.launch { quizRepository.insertQuiz(title, description, grade, url) }
    }

    fun deleteQuiz(quizId: Int) { viewModelScope.launch { quizRepository.deleteQuiz(quizId) } }

    fun toggleStatus(quizId: Int, currentStatus: String) {
        viewModelScope.launch {
            val newStatus = if (currentStatus == "Draft") "Published" else "Draft"
            quizRepository.getQuizById(quizId)?.let { quizRepository.updateQuiz(it.copy(status = newStatus)) }
        }
    }
}