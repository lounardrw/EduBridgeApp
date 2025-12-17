package com.example.edubridge.network

//Petición para registrar un Quiz completado por el alumno.

data class QuizCompletedRequest(
    val quiz_id: Int,
    val user_matricula: String,
    val score_obtenido: Int? = null
)

//Petición para registrar que un alumno accedió o vio un Recurso.
data class ResourceViewRequest(
    val resource_id: Int,
    val user_matricula: String
)