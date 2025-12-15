package com.example.edubridge.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.R
import com.example.edubridge.ui.student.EventType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventsScreen(
    viewModel: TeacherEventsViewModel = viewModel()
) {
    val events by viewModel.events.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddOrEditDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<EventData?>(null) }
    var eventToDelete by remember { mutableStateOf<EventData?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestionar Eventos y Avisos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                eventToEdit = null
                showAddOrEditDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Evento")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                loading -> CircularProgressIndicator()
                error != null -> Text(text = error ?: "Error desconocido")
                events.isEmpty() -> Text("No hay eventos. ¡Agrega uno nuevo!")
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(events, key = { it.id }) { event ->
                            EventCard(
                                event = event,
                                onEditClick = {
                                    eventToEdit = event
                                    showAddOrEditDialog = true
                                },
                                onDeleteClick = { eventToDelete = event }
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Agregar / Editar ---
    if (showAddOrEditDialog) {
        AddOrEditEventDialog(
            event = eventToEdit,
            onDismiss = { showAddOrEditDialog = false },
            onEventConfirm = { title, description ->
                val eventData = if (eventToEdit == null) {
                    EventData(
                        id = 0, // id temporal para crear
                        title = title,
                        description = description,
                        longDescription = description,
                        date = "2025-12-14",
                        categoryColor = Color.Gray,
                        imageResId = R.drawable.evento_futbol,
                        type = EventType.TODO
                    )
                } else {
                    eventToEdit!!.copy(
                        title = title,
                        description = description,
                        longDescription = description
                    )
                }

                scope.launch {
                    if (eventToEdit == null) viewModel.addEvent(eventData) { showAddOrEditDialog = false }
                    else viewModel.updateEvent(eventData) { showAddOrEditDialog = false }
                }
            }
        )
    }

    // --- Confirmar eliminación ---
    // --- Confirmar eliminación ---
    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Deseas eliminar '${event.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.deleteEvent(event.id) { eventToDelete = null }
                        }

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) { Text("Cancelar") }
            }
        )
    }

}

@Composable
fun EventCard(event: EventData, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleLarge)
            Text(event.description, style = MaterialTheme.typography.bodyMedium)
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
            }
        }
    }
}

@Composable
fun AddOrEditEventDialog(
    event: EventData?,
    onDismiss: () -> Unit,
    onEventConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var isTitleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "Nuevo Evento" else "Editar Evento") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) isTitleError = false
                    },
                    label = { Text("Título") },
                    singleLine = true,
                    isError = isTitleError
                )
                if (isTitleError) Text("El título es obligatorio", color = MaterialTheme.colorScheme.error)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) onEventConfirm(title, description)
                else isTitleError = true
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
