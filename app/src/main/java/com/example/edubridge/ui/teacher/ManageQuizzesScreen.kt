package com.example.edubridge.ui.teacher

import androidx.compose.foundation.clickable
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

// ====================================================================
// DATOS SIMULADOS (MOCK DATA) Y ESTADO DEL CRUD
// ====================================================================

data class EditableQuiz(
    val id: Int,
    val title: String,
    val grade: String,
    val status: String // "Draft" (Borrador) o "Published" (Publicado)
)

val initialQuizzes = listOf(
    EditableQuiz(101, "Test de Cinemática", "2° Secundaria", "Published"),
    EditableQuiz(102, "Ecuaciones Cuadráticas", "3° Secundaria", "Draft"),
    EditableQuiz(103, "Conceptos de Redes", "1° Secundaria", "Published")
)

// ====================================================================
// COMPOSABLE PRINCIPAL
// ====================================================================

/**
 * Pantalla de Gestión de Cuestionarios (Luis).
 * Permite al profesor crear nuevos Quizzes y administrarlos (CRUD simulado).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageQuizzesScreen(modifier: Modifier = Modifier) {
    // Estado mutable para la lista de quizzes (simula la BD)
    var quizzes by remember { mutableStateOf(initialQuizzes) }
    var newQuizTitle by remember { mutableStateOf("") }
    var newQuizGrade by remember { mutableStateOf("1° Secundaria") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Cuestionarios") }) }
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
                selectedGrade = newQuizGrade,
                onGradeChange = { newQuizGrade = it },
                onSave = {
                    // Lógica para añadir un nuevo quiz (simulado)
                    val newId = quizzes.maxOfOrNull { it.id }?.plus(1) ?: 1
                    val newQuiz = EditableQuiz(newId, newQuizTitle, newQuizGrade, "Draft")
                    quizzes = quizzes + newQuiz
                    newQuizTitle = "" // Limpiar formulario
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
                items(quizzes) { quiz ->
                    EditableQuizCard(
                        quiz = quiz,
                        onDelete = {
                            quizzes = quizzes.filter { it.id != quiz.id } // Eliminar (simulado)
                        },
                        onEdit = {
                            // Simulación: Cambiar el estado a publicado/borrador
                            quizzes = quizzes.map {
                                if (it.id == quiz.id) {
                                    it.copy(status = if (it.status == "Draft") "Published" else "Draft")
                                } else {
                                    it
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// ====================================================================
// COMPOSABLES AUXILIARES
// ====================================================================

/**
 * Formulario para crear o editar un Quiz.
 */
@OptIn(ExperimentalMaterial3Api::class) // <--- ¡Anotación necesaria agregada aquí!
@Composable
fun QuizCreationForm(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedGrade: String,
    onGradeChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Crear Nuevo Cuestionario", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título del Cuestionario") },
                modifier = Modifier.fillMaxWidth()
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
                    // La propiedad .menuAnchor() requiere ExperimentalMaterial3Api
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
                enabled = title.isNotBlank(),
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
fun EditableQuizCard(quiz: EditableQuiz, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(quiz.title, style = MaterialTheme.typography.titleMedium)
                Text("Grado: ${quiz.grade}", style = MaterialTheme.typography.bodySmall)
                Text("Estado: ${quiz.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (quiz.status == "Published") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Botón de Edición (simula publicar/despublicar)
                IconButton(onClick = onEdit) {
                    Icon(
                        if (quiz.status == "Draft") Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = "Editar/Publicar",
                        tint = if (quiz.status == "Draft") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
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