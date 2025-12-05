package com.example.edubridge.ui.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

        val events = getSampleEvents()

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
fun EventCard(
    event: EventData,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Image(
                painter = painterResource(id = event.imageResId),
                contentDescription = "Cartel del evento: ${event.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Box(modifier = Modifier.fillMaxHeight().width(8.dp).background(event.categoryColor))

                Column(
                    modifier = Modifier.fillMaxHeight().padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = event.date.substringBefore(" "), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = event.categoryColor)
                    Text(text = event.date.substringAfter(" "), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                }

                Column(
                    modifier = Modifier.padding(vertical = 16.dp).padding(end = 16.dp).weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = event.description, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray, lineHeight = 20.sp, maxLines = 3)
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

data class EventData(
    val title: String,
    val description: String,
    val longDescription: String,
    val date: String,
    val categoryColor: Color,
    val imageResId: Int
)

fun getSampleEvents(): List<EventData> {
    return listOf(
        EventData(
            title = "Entrega de Calificaciones",
            description = "Reunión general en el auditorio para la entrega del primer parcial.",
            longDescription = "Se convoca a todos los estudiantes a la reunión general en el auditorio principal para la entrega de las calificaciones correspondientes al primer parcial. El evento se llevará a cabo el día 25 de Enero a las 10:00 AM. Es obligatoria la asistencia con su credencial de estudiante. Se discutirán también los próximos calendarios de exámenes. Recuerden que no se darán informes individuales fuera de esta sesión, por lo que su presencia es de suma importancia. Para cualquier duda, consulten a su tutor asignado.",
            date = "25 ENE",
            categoryColor = Color(0xFF009688),
            imageResId = R.drawable.evento_calificaciones
        ),
        EventData(
            title = "Torneo de Fútbol Interescolar",
            description = "¡Apoya a nuestro equipo en la gran final! No faltes.",
            longDescription = "¡La gran final del Torneo de Fútbol Interescolar ha llegado! Nuestro equipo se enfrentará a los 'Tigres del Norte' en un partido que promete ser épico. Ven con tus amigos y familiares a apoyar a nuestros jugadores. Habrá venta de comida y souvenirs del equipo. ¡No te lo puedes perder el 28 de Enero en el campo principal a partir de las 4:00 PM! ¡Viste los colores de la escuela y trae tu mejor energía!",
            date = "28 ENE",
            categoryColor = Color(0xFFF44336),
            imageResId = R.drawable.evento_futbol
        ),
        EventData(
            title = "Mantenimiento de Plataforma",
            description = "El sistema estará fuera de línea de 10 PM a 11 PM.",
            longDescription = "AVISO IMPORTANTE: Se realizará un mantenimiento programado en los servidores de la plataforma EduBridge. Durante este periodo, el acceso a todas las funcionalidades (calificaciones, biblioteca, salones) no estará disponible. El mantenimiento se llevará a cabo el 30 de Enero, desde las 10:00 PM hasta las 11:00 PM. Agradecemos su comprensión mientras trabajamos para mejorar su experiencia en la plataforma.",
            date = "30 ENE",
            categoryColor = Color(0xFF169600),
            imageResId = R.drawable.evento_mantenimiento
        )
    )
}
