package com.example.edubridge.data.local.entitymodel

data class Event(
    override val id: Int,
    override val title: String,
    override val createdAt: String,
    override val createdBy: Int,
    val description: String,
    val date: String
) : Contenido(
    id = id,
    title = title,
    createdAt = createdAt,
    createdBy = createdBy
)
