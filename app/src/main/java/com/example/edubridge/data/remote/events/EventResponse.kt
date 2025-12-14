package com.example.edubridge.data.remote.events

import com.example.edubridge.data.model.EventDto

data class EventResponse(
    val ok: Boolean,
    val data: List<EventDto>? = null,
    val message: String? = null,
    val error: String? = null
)
