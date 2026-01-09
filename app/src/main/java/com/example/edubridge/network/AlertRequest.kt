package com.example.edubridge.network

//Petición enviada a PHP para registrar una alerta de pánico.
data class AlertRequest(
    val user_id: Int,
    val latitude: Double,
    val longitude: Double
)

//Respuesta genérica de éxito/fallo del servidor PHP.
data class GenericResponse(
    val ok: Boolean,
    val error: String? = null
)