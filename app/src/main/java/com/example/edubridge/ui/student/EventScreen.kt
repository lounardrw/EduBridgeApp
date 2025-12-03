package com.example.edubridge.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.edubridge.ui.theme.EduBridgeTheme

// Modelo de datos para un evento.
data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val type: String // "Aviso", "Convocatoria", "Festival"
)

// Datos de ejemplo para la previsualización y pruebas iniciales.
val sampleEvents = listOf(
    Event(1, "Convocatoria de Becas 2025", "Ya está abierta la convocatoria para las becas de excelencia académica. Fecha límite: 30 de enero.", "Convocatoria"),
    Event(2, "Festival Cultural de Fin de Año", "¡No te pierdas el festival con música, baile y comida! Será en el patio principal.", "Festival"),
    Event(3, "Suspensión de Clases", "Se informa que el próximo viernes no habrá actividades académicas por día festivo.", "Aviso"),
    Event(4, "Torneo de Deportes Interfacultades", "Inscribe a tu equipo en las oficinas de deportes antes del lunes.", "Convocatoria")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onNavigateBack: () -> Unit // Función para regresar a la pantalla anterior
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos y Avisos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleEvents) { event ->
                EventCard(event = event)
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.type,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// --- Previsualización para Android Studio ---
@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    EduBridgeTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            EventsScreen(onNavigateBack = {})
        }
    }
}

