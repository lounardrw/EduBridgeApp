package com.example.edubridge.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.ui.QuizViewModel
import com.example.edubridge.ui.QuizModuleUI


// ====================================================================
// COMPOSABLE PRINCIPAL - PROFESOR
// ====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageQuizzesScreen(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel() // Inyecta el ViewModel de Room
) {
    val quizzes by viewModel.quizzes.collectAsState()

    var newQuizTitle by remember { mutableStateOf("") }
    var newQuizDescription by remember { mutableStateOf("") } // <-- FIX: ESTADO DE DESCRIPCIÓN REINTRODUCIDO
    var newQuizGrade by remember { mutableStateOf("1° Secundaria") }
    var newQuizUrl by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Módulos y Cuestionarios") }) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Formulario de Creación
            QuizCreationForm(
                title = newQuizTitle,
                onTitleChange = { newQuizTitle = it },
                description = newQuizDescription, // <-- PASAR DESCRIPCIÓN
                onDescriptionChange = { newQuizDescription = it }, // <-- CALLBACK DESCRIPCIÓN
                selectedGrade = newQuizGrade,
                onGradeChange = { newQuizGrade = it },
                url = newQuizUrl,
                onUrlChange = { newQuizUrl = it },
                onSave = {
                    // FIX: PASAR LOS CUATRO ARGUMENTOS (title, description, grade, formUrl)
                    viewModel.insertQuiz(newQuizTitle, newQuizDescription, newQuizGrade, newQuizUrl)
                    newQuizTitle = ""
                    newQuizDescription = "" // Limpiar
                    newQuizUrl = ""
                }
            )

            Spacer(Modifier.height(24.dp))
            Text(
                "Cuestionarios Existentes (${quizzes.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))

            // 2. Lista de Quizzes Existentes
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(quizzes, key = { it.id }) { quiz ->
                    EditableQuizCard(
                        quiz = quiz,
                        onDelete = {
                            viewModel.deleteQuiz(quiz.id)
                        },
                        onEdit = {
                            viewModel.toggleStatus(quiz.id, quiz.status)
                        }
                    )
                }
            }
        }
    }
}

// ====================================================================
// AUXILIARES REQUERIDOS (Formulario actualizado con descripción)
// ====================================================================

/**
 * Formulario para crear o editar un Quiz.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCreationForm(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String, // <-- AÑADIDO
    onDescriptionChange: (String) -> Unit, // <-- AÑADIDO
    selectedGrade: String,
    onGradeChange: (String) -> Unit,
    url: String,
    onUrlChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Crear Nuevo Módulo/Tema", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título del Módulo (Ej: Álgebra Básica)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // CAMPO DE DESCRIPCIÓN DEL MÓDULO (Para el alumno)
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción Corta para el Alumno") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // CAMPO DE URL DEL FORMULARIO
            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                label = { Text("URL de Google Forms/Cuestionario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))


            // Selector de Grado (DropDown Menu)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedGrade,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Asignar a Grado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("1° Secundaria", "2° Secundaria", "3° Secundaria").forEach { grade ->
                        DropdownMenuItem(
                            text = { Text(grade) },
                            onClick = {
                                onGradeChange(grade)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSave,
                // Habilitado solo si tiene título, descripción y URL
                enabled = title.isNotBlank() && description.isNotBlank() && url.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Borrador")
            }
        }
    }
}

/**
 * Tarjeta para mostrar un Quiz existente con opciones de CRUD.
 */
@Composable
fun EditableQuizCard(quiz: QuizModuleUI, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(quiz.title, style = MaterialTheme.typography.titleMedium)
                // Muestra la descripción del módulo
                Text("Descripción: ${quiz.description.take(50)}...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Grado: ${quiz.grade}", style = MaterialTheme.typography.bodySmall)
                // Muestra la URL para verificación del profesor
                Text("URL: ${quiz.formUrl.take(30)}...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Estado: ${quiz.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (quiz.status == "Published") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Botón de Publicación/Edición
                IconButton(onClick = onEdit) {
                    val isPublished = quiz.status == "Published"
                    Icon(
                        // Si está en Draft, muestra el icono de 'Publicar' (Save)
                        // Si está publicado, muestra el icono de 'Editar'
                        imageVector = if (!isPublished) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = if (!isPublished) "Publicar Módulo" else "Editar",
                        tint = if (!isPublished) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                }

                // Botón de Borrar
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}