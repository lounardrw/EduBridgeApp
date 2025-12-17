package com.example.edubridge.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.EduBridgeApp
import com.example.edubridge.data.QuizRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


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

    init {
        val quizDao = (application as EduBridgeApp).database.quizDao()
        quizRepository = QuizRepository(quizDao)
    }

    // Expone los Quizzes de la base de datos
    val quizzes: StateFlow<List<QuizModuleUI>> = quizRepository.getAllQuizzes()
        .map { list ->
            list.map { entity ->
                // Mapeo de la Entidad de Room al DTO de la UI
                QuizModuleUI(
                    id = entity.id,
                    title = entity.contenido.title,
                    description = entity.contenido.description,
                    grade = entity.gradeLevel,
                    status = entity.status,
                    formUrl = entity.formUrl
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertQuiz(title: String, description: String, grade: String, formUrl: String) {
        viewModelScope.launch {
            quizRepository.insertQuiz(title, description, grade, formUrl)
        }
    }

    fun deleteQuiz(quizId: Int) {
        viewModelScope.launch {
            quizRepository.deleteQuiz(quizId)
        }
    }
    fun toggleStatus(quizId: Int, currentStatus: String) {
        viewModelScope.launch {
            // 1. Determinar el nuevo estado
            val newStatus = if (currentStatus == "Draft") "Published" else "Draft"

            // 2. Obtener la entidad para mantener los demás campos (como la URL)
            val entity = quizRepository.getQuizById(quizId)

            if (entity != null) {
                // 3. Crear una copia con el nuevo estado
                val updatedEntity = entity.copy(
                    status = newStatus
                    // No necesitamos actualizar el contenido, solo el estado
                )
                // 4. Actualizar la base de datos
                quizRepository.updateQuiz(updatedEntity)
                println("Estado de Quiz ID $quizId cambiado a $newStatus")
            }
        }
    }
}

