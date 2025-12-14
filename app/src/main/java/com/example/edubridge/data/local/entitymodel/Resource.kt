package com.example.edubridge.data.local.entitymodel

data class Resource(
    override val id: Int,
    override val title: String,
    override val createdAt: String,
    override val createdBy: Int,
    val author: String,
    val fileUrl: String,
    val localPath: String? = null
) : Contenido(
    id = id,
    title = title,
    createdAt = createdAt,
    createdBy = createdBy
)
