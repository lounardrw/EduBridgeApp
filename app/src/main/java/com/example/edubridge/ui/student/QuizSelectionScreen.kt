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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.ui.QuizViewModel
import com.example.edubridge.ui.QuizModuleUI
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSelectionScreen(
    grade: String,
    onModuleSelected: (Int) -> Unit, // Callback para navegar al detalle
    viewModel: QuizViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val allQuizzes by viewModel.quizzes.collectAsState()

    // Filtra los quizzes desde Room
    val quizzesForGrade = allQuizzes.filter { it.grade == grade && it.status == "Published" } // Solo publicados

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
                    items(quizzesForGrade, key = { it.id }) { module ->
                        // ACCIÓN: Navegar a la pantalla de detalle, pasando el ID
                        QuizModuleCard(module = module, onClick = {
                            onModuleSelected(module.id)
                        })
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay módulos ni cuestionarios disponibles para $grade.", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

//Tarjeta que muestra la información de un módulo/tema para el alumno.
@Composable
fun QuizModuleCard(module: QuizModuleUI, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                // Título del Módulo
                Text(module.title, style = MaterialTheme.typography.titleMedium)

                // Usamos module.description (propiedad que ahora existe en QuizModuleUI)
                Text(module.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)

                Spacer(Modifier.height(4.dp))
                Text(
                    "Ver detalles y cuestionario",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ver Detalles",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}