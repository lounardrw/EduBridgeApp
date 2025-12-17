package com.example.edubridge.data

import com.example.edubridge.data.local.dao.QuizDao
import com.example.edubridge.data.local.entitymodel.QuizContenido
import com.example.edubridge.data.local.entitymodel.QuizEntity
import kotlinx.coroutines.flow.Flow

class QuizRepository(private val quizDao: QuizDao) {

    fun getAllQuizzes(): Flow<List<QuizEntity>> {
        return quizDao.getAllQuizzes()
    }

    suspend fun insertQuiz(title: String, description: String, grade: String, formUrl: String) {
        val newQuiz = QuizEntity(
            contenido = QuizContenido(title, description),
            gradeLevel = grade,
            status = "Draft",
            formUrl = formUrl
        )
        quizDao.insert(newQuiz)
    }

    suspend fun deleteQuiz(quizId: Int) {
        quizDao.deleteById(quizId)
    }

    suspend fun getQuizById(quizId: Int): QuizEntity? {
        return quizDao.getById(quizId)
    }

    // Método para actualizar una entidad de Quiz (usado para cambiar el estado).

    suspend fun updateQuiz(quizEntity: QuizEntity) {
        quizDao.update(quizEntity)
    }
}
