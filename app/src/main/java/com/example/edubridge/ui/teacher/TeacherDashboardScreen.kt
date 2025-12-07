package com.example.edubridge.ui.teacher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.edubridge.data.PanicAlertRepository

data class ManagementOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// PANTALLA PRINCIPAL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    // Funciones de navegación para cada módulo (vienen desde MainActivity).
    onManageLibrary: () -> Unit,
    onManageEvents: () -> Unit,
    onManageQuizzes: () -> Unit,
    onViewAlert: () -> Unit
) {
    // Escucha el estado de alerta en tiempo real del repositorio.
    // Si un alumno presiona el pánico, 'activeAlert' cambia y la UI se actualiza (recomposición).
    val activeAlert by PanicAlertRepository.activeAlert.collectAsState(initial = null)

    // Lista de opciones de gestión disponibles para el profesor.
    val managementOptions = listOf(
        ManagementOption(
            title = "Gestionar Biblioteca",
            description = "Sube, edita o elimina recursos PDF.",
            icon = Icons.Default.Book,
            onClick = onManageLibrary
        ),
        ManagementOption(
            title = "Gestionar Eventos",
            description = "Publica avisos y actualiza el calendario.",
            icon = Icons.Default.CalendarToday,
            onClick = onManageEvents
        ),
        ManagementOption(
            title = "Gestionar Cuestionarios",
            description = "Crea y asigna cuestionarios a los grados.",
            icon = Icons.Default.Quiz,
            onClick = onManageQuizzes
        )
    )

    // Estructura visual de la pantalla.
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Panel del Profesor") })
        }
    ) { innerPadding ->
        // Lista desplazable de opciones.
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Muestra la tarjeta de alerta SÓLO si hay una alerta activa.
            if (activeAlert != null) {
                item {
                    AlertCard(onClick = onViewAlert)
                }
            }

            // 2. Muestra las tarjetas de gestión.
            items(managementOptions) { option ->
                ManagementCard(option = option)
            }
        }
    }
}
// Composables auxiliares
// Tarjeta reutilizable para cada opción de gestión.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementCard(option: ManagementOption) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = option.onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Column {
                Text(text = option.title, style = MaterialTheme.typography.titleLarge)
                Text(text = option.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// Tarjeta especial para mostrar la ALERTA DE PÁNICO.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), // Fondo de color suave de error
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(40.dp))
            Column {
                Text("¡ALERTA DE PÁNICO ACTIVA!", style = MaterialTheme.typography.titleLarge)
                Text("Un alumno necesita ayuda. Haz clic para ver la ubicación.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

