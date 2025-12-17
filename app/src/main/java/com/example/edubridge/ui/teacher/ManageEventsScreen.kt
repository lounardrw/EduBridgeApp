package com.example.edubridge.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.ui.student.EventModuleUI
import com.example.edubridge.ui.student.EventType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

//Obtiene la fecha actual en formato YYYY-MM-DD.
private fun getTodayDateString(): String {
    // Usamos el formato ISO de fecha para MySQL
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return formatter.format(Date())
}

// PANTALLA PRINCIPAL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventsScreen(viewModel: TeacherEventsViewModel = viewModel()) {
    // Escuchar la lista de eventos desde el ViewModel
    val events by viewModel.events.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    //Capturamos el color Primary AQUI, en el contexto @Composable
    val primaryColor = MaterialTheme.colorScheme.primary

    var showAddOrEditDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<EventModuleUI?>(null) }
    var eventToDelete by remember { mutableStateOf<EventModuleUI?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Eventos y Anuncios") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    eventToEdit = null
                    showAddOrEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Evento", tint = MaterialTheme.colorScheme.onSecondary)
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
                loading && events.isEmpty() -> CircularProgressIndicator()
                error != null -> Text("ERROR: $error", color = MaterialTheme.colorScheme.error)
                events.isEmpty() -> Text("No hay eventos ni anuncios. ¡Agrega uno nuevo!")
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

    if (showAddOrEditDialog) {
        AddOrEditEventDialog(
            event = eventToEdit,
            onDismiss = { showAddOrEditDialog = false },
            onEventConfirm = { title, description, date ->
                if (eventToEdit == null) {
                    // CREAR NUEVO
                    val newEvent = EventModuleUI(
                        id = 0, // ID 0 para que MySQL lo genere
                        title = title,
                        description = description,
                        longDescription = description,
                        date = date,
                        categoryColor = primaryColor,
                        type = EventType.AVISOS
                    )
                    viewModel.addEvent(newEvent) // Llamada al Web Service
                } else {
                    // EDITAR EXISTENTE
                    val updatedEvent = eventToEdit!!.copy(
                        title = title,
                        description = description,
                        longDescription = description,
                        date = date
                    )
                    viewModel.updateEvent(updatedEvent) // Llamada al Web Service
                }
                showAddOrEditDialog = false
            }
        )
    }

    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el evento '${event.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEvent(event.id) // Llamada al Web Service
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

//La tarjeta ahora usa EventModuleUI
@Composable
fun EventCard(event: EventModuleUI, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Mostrar la fecha del evento
                Text(
                    text = "Fecha: ${event.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar Evento",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar Evento",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditEventDialog(
    event: EventModuleUI?,
    onDismiss: () -> Unit,
    onEventConfirm: (title: String, description: String, date: String) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var dateString by remember { mutableStateOf(event?.date ?: getTodayDateString()) }
    var isTitleError by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        if (event != null) {
            title = event.title
            description = event.description
            dateString = event.date
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "Nuevo Evento o Anuncio" else "Editar Evento") },
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
                if (isTitleError) {
                    Text("El título es obligatorio", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.height(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // CAMPO DE FECHA
                OutlinedTextField(
                    value = dateString,
                    onValueChange = { dateString = it },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) {
                    onEventConfirm(title, description, dateString)
                } else {
                    isTitleError = true
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
