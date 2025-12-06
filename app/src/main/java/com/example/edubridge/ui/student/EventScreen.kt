package com.example.edubridge.ui.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.edubridge.R

// --- PASO CLAVE: AÑADIMOS EL PARÁMETRO MODIFIER ---
@Composable
fun EventsScreen(modifier: Modifier = Modifier) {
    // Estado para guardar el evento seleccionado que se mostrará en el diálogo.
    var selectedEvent by remember { mutableStateOf<EventData?>(null) }
    var currentFilter by remember { mutableStateOf(EventType.TODO) }

    selectedEvent?.let { event ->
        EventDetailDialog(
            event = event,
            onDismiss = { selectedEvent = null }
        )
    }

    LazyColumn(
        // --- Y LO APLICAMOS AQUÍ ---
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Eventos y Avisos",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F2F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredEvents.isEmpty()) {
                item {
                    Text(
                        text = "No hay eventos en esta categoría.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                items(filteredEvents) { event ->
                    EventCard(
                        event = event,
                        onCardClick = { selectedEvent = event }
                    )
                }
            }
        }
    }
}


        // Usamos items(events) directamente para mejor rendimiento
        items(events.size) { index ->
            val event = events[index]
            EventCard(
                event = event,
                onCardClick = { selectedEvent = event }
            )
        }
    }
}

// El resto de tu archivo ya está perfecto, no necesita cambios.
@Composable
fun EventCard(event: EventData, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Image(
                painter = painterResource(id = event.imageResId),
                contentDescription = "Cartel del evento: ${event.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(event.categoryColor))

                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = event.date.substringBefore(" "), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = event.categoryColor)
                    Text(text = event.date.substringAfter(" "), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                }

                Column(
                    modifier = Modifier.padding(vertical = 12.dp).padding(end = 12.dp).weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = event.description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray, lineHeight = 16.sp, maxLines = 2)
                }
            }
        }
    }
}

@Composable
fun EventDetailDialog(event: EventData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(id = event.imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = event.longDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

// ----- DATOS (sin cambios) -----
data class EventData(
    val title: String,
    val description: String,
    val longDescription: String,
    val date: String,
    val categoryColor: Color,
    val imageResId: Int,
    val type: EventType
)

fun getSampleEvents(): List<EventData> {
    return listOf(
        EventData(
            title = "Beca de Excelencia Académica",
            description = "Aplica ya a la beca por promedio para el siguiente semestre.",
            longDescription = "La convocatoria para la Beca de Excelencia Académica ya está abierta. Los requisitos incluyen un promedio mínimo de 9.5 y no tener materias reprobadas. La fecha límite para entregar documentos es el 15 de Febrero. Consulta las bases completas en el portal.",
            date = "15 FEB",
            categoryColor = Color(0xFF0D47A1),
            imageResId = R.drawable.evento_calificaciones,
            type = EventType.BECAS
        ),
        EventData(
            title = "Torneo de Fútbol Interescolar",
            description = "¡Apoya a nuestro equipo en la gran final! No faltes.",
            longDescription = "¡La gran final del Torneo de Fútbol Interescolar ha llegado! Nuestro equipo se enfrentará a los 'Tigres del Norte' en un partido que promete ser épico...",
            date = "28 ENE",
            categoryColor = Color(0xFFF44336),
            imageResId = R.drawable.evento_futbol,
            type = EventType.PRÓXIMOS
        ),
        EventData(
            title = "Mantenimiento de Plataforma",
            description = "El sistema estará fuera de línea de 10 PM a 11 PM.",
            longDescription = "AVISO IMPORTANTE: Se realizará un mantenimiento programado en los servidores de la plataforma EduBridge. Durante este periodo, el acceso no estará disponible...",
            date = "30 ENE",
            categoryColor = Color(0xFF169600),
            imageResId = R.drawable.evento_mantenimiento,
            type = EventType.AVISOS
        )
    )
}
