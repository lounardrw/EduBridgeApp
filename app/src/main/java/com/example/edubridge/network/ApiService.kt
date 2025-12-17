package com.example.edubridge.network

//Servicio Lazy para acceder a la API (Singleton).
object ApiService {
    val api: EduBridgeApi by lazy {
        RetrofitClient.instance.create(EduBridgeApi::class.java)
    }
}