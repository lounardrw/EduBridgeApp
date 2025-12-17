package com.example.edubridge.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.ui.student.QuizModuleDetailViewModel.Factory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizModuleDetailScreen(
    quizId: Int,
    viewModel: QuizModuleDetailViewModel = viewModel(factory = Factory(quizId))
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    // Usamos el operador seguro en el título de la barra
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(uiState.quiz?.title ?: "Detalle del Módulo") })
        }
    ) { innerPadding ->

        // Manejo de estado: si el quiz no es nulo, lo desempacamos
        val quiz = uiState.quiz

        when {
            uiState.loading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(uiState.error ?: "Error al cargar.", color = MaterialTheme.colorScheme.error)
                }
            }
            // Condición simple: si 'quiz' no es nulo (ya desempacado en la línea 30)
            quiz != null -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Grado Asignado: ${quiz.grade}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))

                    // Descripción Detallada del Tema
                    Text(
                        "Descripción del Módulo:",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        quiz.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(32.dp))

                    Divider()
                    Spacer(Modifier.height(32.dp))

                    // Sección del Cuestionario (El Link)
                    Text(
                        "Cuestionario del Tema:",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (quiz.formUrl.startsWith("http")) {
                                uriHandler.openUri(quiz.formUrl)
                            }
                        },
                        // Se deshabilita si la URL es inválida o el quiz no está publicado
                        enabled = quiz.formUrl.startsWith("http") && quiz.status == "Published", // Acceso directo
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Iniciar Cuestionario Externo", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Estado: ${quiz.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (quiz.status == "Published") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
