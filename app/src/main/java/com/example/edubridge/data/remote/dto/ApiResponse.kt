package com.example.edubridge.data.remote.dto

data class ApiResponse<T>(
    val ok: Boolean,
    val data: T
)
