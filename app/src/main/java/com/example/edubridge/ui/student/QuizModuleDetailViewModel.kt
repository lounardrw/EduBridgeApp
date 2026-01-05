package com.example.edubridge.ui.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.edubridge.EduBridgeApp
import com.example.edubridge.data.QuizRepository
import com.example.edubridge.ui.QuizModuleUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizDetailUiState(
    val quiz: QuizModuleUI? = null,
    val loading: Boolean = true,
    val error: String? = null
)

class QuizModuleDetailViewModel(
    application: Application,
    private val quizId: Int
) : AndroidViewModel(application) {

    private val repository: QuizRepository

    private val _uiState = MutableStateFlow(QuizDetailUiState())
    val uiState: StateFlow<QuizDetailUiState> = _uiState.asStateFlow()

    init {
        val quizDao = (application as EduBridgeApp).database.quizDao()
        repository = QuizRepository(quizDao)
        loadQuizDetails()
    }

    private fun loadQuizDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val entity = repository.getQuizById(quizId)

                if (entity != null) {
                    // El mapeo funciona si las propiedades existen en QuizEntity (lo hacen)
                    val uiModel = QuizModuleUI(
                        id = entity.id,
                        title = entity.contenido.title,
                        description = entity.contenido.description,
                        grade = entity.gradeLevel,
                        status = entity.status,
                        formUrl = entity.formUrl
                    )
                    _uiState.update { it.copy(quiz = uiModel, loading = false) }
                } else {
                    _uiState.update { it.copy(loading = false, error = "Módulo no encontrado.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    class Factory(private val quizId: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
            return QuizModuleDetailViewModel(application, quizId) as T
        }
    }
}
