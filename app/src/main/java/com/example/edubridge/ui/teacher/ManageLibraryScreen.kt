package com.example.edubridge.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.ui.LibraryViewModel
import com.example.edubridge.ui.student.ResourceCard // Reutilizamos el Card del alumno

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLibraryScreen(
    viewModel: LibraryViewModel = viewModel() // Inyecta el mismo ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estado para manejar los campos de texto del nuevo recurso
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Gestión de Biblioteca",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))

        // --- FORMULARIO PARA AÑADIR RECURSO (CREATE) ---
        // Este formulario solo lo ve el profesor. El alumno nunca llega a esta pantalla.
        // Esto cumple con "Control de visibilidad para botones administrativos".
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título del libro") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL del recurso") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.addResource(title, author, url)
                // Limpiar campos después de añadir
                title = ""
                author = ""
                url = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Subir Nuevo Recurso")
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // --- LISTA DE RECURSOS EXISTENTES (READ) ---
        Text("Recursos Actuales", style = MaterialTheme.typography.titleLarge)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.resources) { resource ->
                // Aquí podrías crear un `ResourceCardTeacher` con botones de Editar/Borrar
                // Por ahora, reutilizamos el del alumno para mostrar la lista.
                ResourceCard(resource = resource, onClick = { /* El profesor no necesita click */ })
            }
        }
    }
}