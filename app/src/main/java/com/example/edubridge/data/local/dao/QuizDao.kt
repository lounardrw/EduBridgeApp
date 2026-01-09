package com.example.edubridge.data.local.dao

import androidx.room.*
import com.example.edubridge.data.local.entitymodel.QuizEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao : BaseDao<QuizEntity> {
    @Query("SELECT * FROM quizzes")
    fun getAllQuizzes(): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE id = :quizId LIMIT 1")
    suspend fun getById(quizId: Int): QuizEntity?

    @Query("DELETE FROM quizzes WHERE id = :quizId")
    suspend fun deleteById(quizId: Int)
}