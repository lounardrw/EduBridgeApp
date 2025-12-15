package com.example.edubridge.data.remote


import com.example.edubridge.data.local.entitymodel.Event
import com.example.edubridge.data.model.EventDto
import com.example.edubridge.data.model.UserResponse
import com.example.edubridge.data.remote.dto.ApiResponse
import com.example.edubridge.data.remote.events.EventRequest
import com.example.edubridge.data.remote.events.EventResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.Path
import retrofit2.http.Query
interface ApiService {

    @POST("auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<UserResponse>
    @GET("events/list.php")

    suspend fun getEventos(): EventResponse

    @POST("events/create.php")
        suspend fun createEvent(
            @Body request: EventRequest
        ): Response<EventResponse>

        @PUT("events/update.php")
        suspend fun updateEvent(
            @Body request: EventRequest
        ): Response<EventResponse>

    @HTTP(method = "DELETE", path = "events/delete.php", hasBody = true)
    suspend fun deleteEvent(@Body body: Map<String, Int>)



}




