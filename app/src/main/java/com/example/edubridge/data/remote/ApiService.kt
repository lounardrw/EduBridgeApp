package com.example.edubridge.data.remote


import com.example.edubridge.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<UserResponse>
}
