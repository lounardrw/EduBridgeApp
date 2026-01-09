package com.example.edubridge.network

import com.example.edubridge.data.model.LoginResponse
import com.example.edubridge.data.model.EventDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.HTTP

// DTO para la respuesta de verificación de alerta
data class AlertCheckResponse(
    val ok: Boolean,
    val active: Boolean? = false,
    val data: AlertCheckData? = null,
    val error: String? = null
)

data class AlertCheckData(
    val user_id: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)
//Interfaz para definir los Endpoints de la API .
interface EduBridgeApi {

    @POST("auth/login.php")
    suspend fun loginUser(
        @Body request: com.example.edubridge.data.model.LoginRequest
    ): Response<LoginResponse>

    @GET("events/list.php")
    suspend fun getEvents(): Response<List<EventDto>>

    @POST("panic/create.php")
    suspend fun createPanicAlert(
        @Body request: AlertRequest
    ): Response<GenericResponse>

    @POST("panic/clear_alert.php")
    suspend fun clearPanicAlert(@Body body: Map<String, Int>): Response<GenericResponse>

    @GET("panic/check_alert.php")
    suspend fun checkActiveAlert(): Response<AlertCheckResponse>

    @POST("aulas/quiz_completado.php")
    suspend fun logQuizCompleted(
        @Body request: QuizCompletedRequest
    ): Response<GenericResponse>

    @POST("biblioteca/recurso_visto.php")
    suspend fun logResourceView(
        @Body request: ResourceViewRequest
    ): Response<GenericResponse>

    @POST("events/create.php")
    suspend fun createEvent(
        @Body request: EventRequest
    ): Response<GenericResponse>

    @PUT("events/update.php")
    suspend fun updateEvent(
        @Body request: EventRequest
    ): Response<GenericResponse>

    @HTTP(method = "DELETE", path = "events/delete.php", hasBody = true)
    suspend fun deleteEvent(@Body body: Map<String, Int>): Response<GenericResponse>
}