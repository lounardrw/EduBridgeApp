package com.example.edubridge.data.model

data class Resource(
    val id: String,
    val title: String,
    val author: String,
    val fileUrl: String,

    val isDownloaded: Boolean = false,
    val localFilePath: String? = null
)