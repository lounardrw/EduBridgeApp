package com.example.edubridge.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla de selección de cuestionarios/módulos para un grado específico.
 * El alumno elige aquí qué módulo (ej. Matemáticas, IA) desea estudiar.
 * @param grade El grado seleccionado desde AulasScreen.
 */
@Composable
fun QuizSelectionScreen(grade: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Módulos de $grade",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Aquí el alumno podrá elegir entre diferentes módulos de Matemáticas, Ciencias e IA para tomar sus cuestionarios.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = { /* TODO: Lógica para iniciar el quizz */ }) {
            Text("Iniciar Quizz de Matemáticas (Demo)")
        }
    }
}