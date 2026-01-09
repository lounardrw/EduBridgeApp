package com.example.edubridge.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.ui.QuizViewModel
import com.example.edubridge.ui.QuizModuleUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageQuizzesScreen(viewModel: QuizViewModel = viewModel()) {
    val quizzes by viewModel.filteredQuizzes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Estados para el formulario de edición/creación
    var editingQuizId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("1° Secundaria") }
    var url by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GESTIÓN DE CUESTIONARIOS", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
        ) {
            // Título Homogéneo
            Text(
                text = "Administración de Módulos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Crea o edita el contenido de tus exámenes",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            // Punto 4: Buscador para organizar datos
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre de examen...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2E7D32)) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2E7D32)
                )
            )

            Spacer(Modifier.height(16.dp))

            // Formulario de Entrada (Punto 1 y 3)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (editingQuizId == null) "Nuevo Cuestionario" else "Editando Contenido",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título del Examen") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción corta") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("Enlace de Google Forms") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Punto 3: Selector de Grados
                    GradeSelector(selectedGrade = grade, onGradeSelected = { grade = it })

                    Button(
                        onClick = {
                            if (editingQuizId == null) {
                                viewModel.insertQuiz(title, description, grade, url)
                            } else {
                                viewModel.updateQuizContent(editingQuizId!!, title, description, grade, url)
                                editingQuizId = null
                            }
                            // Resetear campos
                            title = ""; description = ""; url = ""; grade = "1° Secundaria"
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(if (editingQuizId == null) "GUARDAR CUESTIONARIO" else "ACTUALIZAR DATOS", fontWeight = FontWeight.Bold)
                    }

                    if (editingQuizId != null) {
                        TextButton(
                            onClick = { editingQuizId = null; title = ""; description = ""; url = ""; grade = "1° Secundaria" },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Cancelar edición", color = Color.Red)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Lista de Cuestionarios
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(quizzes, key = { it.id }) { quiz ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(quiz.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(quiz.grade, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text(
                                    text = if(quiz.status == "Published") "Visible para alumnos" else "Borrador (Oculto)",
                                    color = if(quiz.status == "Published") Color(0xFF2E7D32) else Color.Red,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Botón Editar Contenido
                            IconButton(onClick = {
                                editingQuizId = quiz.id
                                title = quiz.title
                                description = quiz.description
                                url = quiz.formUrl
                                grade = quiz.grade
                            }) {
                                Icon(Icons.Default.Edit, "Editar", tint = Color(0xFF2E7D32))
                            }

                            // Botón Visibilidad
                            IconButton(onClick = { viewModel.toggleStatus(quiz.id, quiz.status) }) {
                                Icon(
                                    imageVector = if(quiz.status == "Published") Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Estado",
                                    tint = Color.Gray
                                )
                            }

                            // Botón Eliminar
                            IconButton(onClick = { viewModel.deleteQuiz(quiz.id) }) {
                                Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GradeSelector(selectedGrade: String, onGradeSelected: (String) -> Unit) {
    val grades = listOf("1° Secundaria", "2° Secundaria", "3° Secundaria")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Grado Destino: $selectedGrade", color = Color.DarkGray)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            grades.forEach { grade ->
                DropdownMenuItem(
                    text = { Text(grade) },
                    onClick = {
                        onGradeSelected(grade)
                        expanded = false
                    }
                )
            }
        }
    }
}