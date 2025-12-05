package com.example.edubridge.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// =======================================================
// MODELOS DE DATOS (DATA CLASSES) - TAREA DE LUIS
// Estos modelos representan las estructuras JSON del backend (Express.js)
// =======================================================

/**
 * DTO (Data Transfer Object) para el cuerpo de la petición de Login.
 * @param email Correo electrónico del usuario.
 * @param password Contraseña del usuario.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Modelo para la respuesta de inicio de sesión del servidor.
 * @param success Indica si el login fue exitoso.
 * @param token Token de autenticación (JWT).
 * @param userRole Rol del usuario ("student" o "teacher").
 */
data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val userRole: String?
)

/**
 * Modelo para un evento o aviso.
 * @param id Identificador único del evento.
 * @param title Título del evento.
 * @param date Fecha del evento.
 * @param description Descripción detallada.
 */
data class EventModel(
    val id: Int,
    val title: String,
    val date: String,
    val description: String
)

// =======================================================
// CLIENTE Y SERVICIO RETROFIT
// =======================================================

/**
 * Cliente Singleton para Retrofit.
 * Se encarga de inicializar la conexión con el servidor.
 */
object RetrofitClient {

    // IP local 10.0.2.2 es el alias del emulador para acceder a 127.0.0.1 (localhost)
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    // Interceptor para ver las peticiones y respuestas en el Logcat (útil para debug)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Muestra todo el cuerpo de la petición/respuesta
    }

    // Cliente HTTP configurado
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Instancia lazy (creada solo cuando se necesita) de Retrofit.
     */
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Añade el cliente OkHttp con el logger
            .addConverterFactory(GsonConverterFactory.create()) // Permite convertir JSON a objetos Kotlin
            .build()
    }
}

// Interfaz para definir los Endpoints de la API - TAREA DE LUIS
interface EduBridgeApi {

    // Tarea de Luis: Endpoint JSON /api/login
    // CORREGIDO: Ahora usa @Body para enviar el objeto LoginRequest (email y password)
    @POST("login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Tarea de Luis: Endpoint JSON /api/eventos
    @GET("eventos")
    suspend fun getEvents(): Response<List<EventModel>>
}

// Servicio Lazy para acceder a la API
object ApiService {
    val api: EduBridgeApi by lazy {
        RetrofitClient.instance.create(EduBridgeApi::class.java)
    }
}