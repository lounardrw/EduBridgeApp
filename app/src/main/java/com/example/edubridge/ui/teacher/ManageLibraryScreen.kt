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
import com.example.edubridge.ui.LibraryViewModel
import com.example.edubridge.data.model.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLibraryScreen(viewModel: LibraryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estados para el formulario
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }

    // Selector de archivos para asegurar persistencia (Punto 1 y 2)
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, takeFlags)
                url = it.toString()
            } catch (e: Exception) {
                url = it.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("GESTIÓN BIBLIOTECA", fontWeight = FontWeight.Black) })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FA)).padding(16.dp)) {

            Text("Control de Libros y Links", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)

            // Buscador
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Buscar recurso...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(16.dp)
            )

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (editingId == null) "Registrar Nuevo" else "Actualizar Recurso", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))

                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor / Fuente") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            label = { Text("URL o Ruta de Archivo") },
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        Spacer(Modifier.width(8.dp))
                        // Botón para adjuntar archivo PDF/DOC local
                        IconButton(onClick = { fileLauncher.launch(arrayOf("*/*")) }) {
                            Icon(Icons.Default.AttachFile, "Adjuntar", tint = Color(0xFF2E7D32))
                        }
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && url.isNotBlank()) {
                                viewModel.saveResource(editingId, title, author, url)
                                title = ""; author = ""; url = ""; editingId = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(if (editingId == null) "PUBLICAR EN BIBLIOTECA" else "GUARDAR CAMBIOS")
                    }

                    if (editingId != null) {
                        TextButton(onClick = { editingId = null; title = ""; author = ""; url = "" }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Cancelar Edición", color = Color.Red)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Lista (Diseño limpio sin IntrinsicSize)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(uiState.filteredResources, key = { it.id }) { res ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(res.title, fontWeight = FontWeight.Bold)
                                Text(res.author, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                if (res.url.startsWith("http")) {
                                    Text("Link Externo", fontSize = 10.sp, color = Color(0xFF0288D1), fontWeight = FontWeight.Bold)
                                }
                            }
                            IconButton(onClick = {
                                editingId = res.id; title = res.title; author = res.author; url = res.url
                            }) { Icon(Icons.Default.Edit, null, tint = Color(0xFF2E7D32)) }

                            IconButton(onClick = { viewModel.deleteResource(res.id) }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}