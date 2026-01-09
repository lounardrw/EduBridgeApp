package com.example.edubridge.data

import com.example.edubridge.data.local.dao.QuizDao
import com.example.edubridge.data.local.entitymodel.QuizContenido
import com.example.edubridge.data.local.entitymodel.QuizEntity

class QuizRepository(private val quizDao: QuizDao) {

    fun getAllQuizzes() = quizDao.getAllQuizzes()

    // SOLUCIÓN AL ERROR 2: Método para buscar por ID
    suspend fun getQuizById(id: Int) = quizDao.getById(id)

    suspend fun insertQuiz(title: String, description: String, grade: String, formUrl: String) {
        val newQuiz = QuizEntity(
            contenido = QuizContenido(title, description),
            gradeLevel = grade,
            status = "Draft",
            formUrl = formUrl
        )
        quizDao.insert(newQuiz)
    }

    // SOLUCIÓN AL ERROR 3: Método para actualizar
    suspend fun updateQuiz(quiz: QuizEntity) = quizDao.update(quiz)

    suspend fun deleteQuiz(quizId: Int) = quizDao.deleteById(quizId)
}