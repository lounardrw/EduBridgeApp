package com.example.edubridge.ui.teacher

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment

// PANTALLA PRINCIPAL

@Composable
fun TeacherDashboardScreen(
    // ESTOS SON LOS PARÁMETROS QUE NECESITAS QUE EL COMPILADOR VEA EN TU RAMA:
    onManageLibrary: () -> Unit,
    onManageEvents: () -> Unit,
    onManageQuizzes: () -> Unit, // Tarea de Luis
    onViewAlert: () -> Unit, // Tarea de Luis
    modifier: Modifier = Modifier
) {
    // La implementación completa del Scaffold y las tarjetas de KAREN irán aquí.

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Panel del Profesor (UI de Karen pendiente)", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
    }
}