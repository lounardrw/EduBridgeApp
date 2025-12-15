package com.example.edubridge.ui.student

import androidx.compose.ui.graphics.Color
import com.example.edubridge.R
import com.example.edubridge.data.model.EventDto

fun EventDto.toEventData(): EventData {

    // Inferimos el tipo desde el título (temporal)
    val eventType = when {
        title.contains("beca", ignoreCase = true) -> EventType.BECAS
        title.contains("aviso", ignoreCase = true) -> EventType.AVISOS
        title.contains("próximo", ignoreCase = true) ||
                title.contains("proximo", ignoreCase = true) -> EventType.PRÓXIMOS
        else -> EventType.TODO
    }

    val color = when (eventType) {
        EventType.BECAS -> Color(0xFF0D47A1)
        EventType.AVISOS -> Color(0xFF169600)
        EventType.PRÓXIMOS -> Color(0xFFF44336)
        EventType.TODO -> Color.Gray
    }

    return EventData(
        title = title,
        description = description ?: "",
        longDescription = description ?: "Sin descripción",
        date = date,
        categoryColor = color,
        imageResId = R.drawable.evento_futbol, // fija por ahora
        type = eventType
    )
}
