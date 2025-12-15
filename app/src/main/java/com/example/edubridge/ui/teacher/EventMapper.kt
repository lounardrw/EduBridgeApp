package com.example.edubridge.ui.teacher

import com.example.edubridge.data.model.EventDto
import androidx.compose.ui.graphics.Color
import com.example.edubridge.ui.student.EventType
import com.example.edubridge.R

fun EventDto.toEventData(): EventData {
    return EventData(
        id = this.id ?: 0,
        title = this.title ?: "",
        description = this.description ?: "",
        longDescription = this.description ?: "",
        date = this.date ?: "",
        categoryColor = Color.Gray,
        imageResId = R.drawable.evento_futbol,
        type = EventType.TODO
    )
}
