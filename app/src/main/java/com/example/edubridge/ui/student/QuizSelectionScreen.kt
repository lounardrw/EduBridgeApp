package com.example.edubridge.ui.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ====================================================================
// DATOS SIMULADOS (MOCK DATA)
// Estos datos simulan los quizzes que deben ser visibles para el alumno.
// ====================================================================

data class QuizModule(
    val id: Int,
    val name: String,
    val description: String,
    val gradeLevel: String,
    val questions: Int
)

val mockQuizzes = listOf(
    QuizModule(1, "Álgebra Básica (Publicado)", "Ecuaciones de primer grado y polinomios.", "1° Secundaria", 15),
    QuizModule(2, "Introducción a la IA", "Conceptos básicos de Machine Learning.", "1° Secundaria", 10),
    QuizModule(3, "Física Clásica", "Leyes de Newton y cinemática.", "2° Secundaria", 20),
    QuizModule(4, "Geometría Analítica", "Cónicas y vectores.", "3° Secundaria", 18),
    QuizModule(5, "Programación con Python", "Fundamentos y estructuras de control.", "3° Secundaria", 12)
)

// ====================================================================
// COMPOSABLE PRINCIPAL
// ====================================================================

/**
 * Pantalla de selección de cuestionarios/módulos para un grado específico.
 * Muestra una lista de Quizzes disponibles para el alumno.
 * @param grade El grado seleccionado desde AulasScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSelectionScreen(grade: String, modifier: Modifier = Modifier) {
    // Filtra los quizzes para mostrar solo los de este grado.
    val quizzesForGrade = mockQuizzes.filter { it.gradeLevel == grade }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Módulos de $grade") })
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            if (quizzesForGrade.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quizzesForGrade) { module ->
                        QuizModuleCard(module = module)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay cuestionarios disponibles para $grade.", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

// ====================================================================
// COMPOSABLES AUXILIARES
// ====================================================================

/**
 * Tarjeta que muestra la información de un módulo de Quiz.
 */
@Composable
fun QuizModuleCard(module: QuizModule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { /* TODO: Navegar a la pantalla de inicio de Quiz */ }),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(module.name, style = MaterialTheme.typography.titleMedium)
                Text(module.description, style = MaterialTheme.typography.bodySmall)
                Text("${module.questions} Preguntas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Iniciar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}