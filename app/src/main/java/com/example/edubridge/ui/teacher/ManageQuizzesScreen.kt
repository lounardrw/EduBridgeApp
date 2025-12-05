package com.example.edubridge.ui.teacher

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla de Gestión de Cuestionarios.
 * Módulo para que el profesor cree y administre los Quizzes de la plataforma.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageQuizzesScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Cuestionarios") }) }
    ) { innerPadding ->
        // Contenedor centrado para el contenido del stub.
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Módulo de Creación y Asignación de Quizzes",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}