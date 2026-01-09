package com.example.edubridge.ui.teacher

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.ui.student.EventModuleUI
import com.example.edubridge.ui.student.EventType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventsScreen(viewModel: TeacherEventsViewModel = viewModel()) {
    // Escuchamos la lista filtrada y la búsqueda
    val filteredEvents by viewModel.filteredEvents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<EventModuleUI?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GESTIÓN EDUCATIVA", fontWeight = FontWeight.Black) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { eventToEdit = null; showDialog = true },
                containerColor = Color(0xFF2E7D32)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FA)).padding(16.dp)) {

            Text("Panel de Comunicados", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            Text("Administra avisos, becas y eventos escolares", color = Color.Gray, fontSize = 14.sp)

            Spacer(Modifier.height(16.dp))

            // --- NUEVO: BARRA DE BÚSQUEDA ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar comunicados por título...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2E7D32)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, null, tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Lista Filtrada
            if (filteredEvents.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron comunicados.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredEvents, key = { it.id }) { event ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                val isDoc = event.imageUrl?.let { it.contains("document") || it.contains(".pdf") } ?: false
                                Icon(
                                    imageVector = if (isDoc) Icons.Default.Description else Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(event.title, fontWeight = FontWeight.Bold)
                                    Text(event.type.displayName, color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelSmall)
                                }
                                IconButton(onClick = { eventToEdit = event; showDialog = true }) {
                                    Icon(Icons.Default.Edit, null, tint = Color(0xFF2E7D32))
                                }
                                IconButton(onClick = { viewModel.deleteEvent(event.id) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        EventEditorDialog(
            event = eventToEdit,
            onDismiss = { showDialog = false },
            onConfirm = { id, title, desc, type, uri ->
                if (id == null) viewModel.addEvent(title, desc, type, uri)
                else viewModel.updateEvent(id, title, desc, type, uri)
                showDialog = false
            }
        )
    }
}

// ... (La función EventEditorDialog se mantiene igual)
@Composable
fun EventEditorDialog(event: EventModuleUI?, onDismiss: () -> Unit, onConfirm: (Int?, String, String, EventType, String) -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(event?.title ?: "") }
    var desc by remember { mutableStateOf(event?.description ?: "") }
    var fileUri by remember { mutableStateOf(event?.imageUrl ?: "") }
    var type by remember { mutableStateOf(event?.type ?: EventType.AVISOS) }
    var expanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, takeFlags)
                fileUri = it.toString()
            } catch (e: Exception) { fileUri = it.toString() }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "Nuevo Comunicado" else "Editar Comunicado", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { launcher.launch(arrayOf("*/*")) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                    Icon(Icons.Default.UploadFile, null); Spacer(Modifier.width(8.dp)); Text("Cargar Multimedia")
                }
                Box {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text("Sección: ${type.displayName}") }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        EventType.values().filter { it != EventType.TODO }.forEach { t ->
                            DropdownMenuItem(text = { Text(t.displayName) }, onClick = { type = t; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(event?.id, title, desc, type, fileUri) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                Text("GUARDAR")
            }
        }
    )
}