package com.example.edubridge.ui.student

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.ui.LibraryViewModel
import com.example.edubridge.data.model.Resource
import com.example.edubridge.filestream.DownloadManager
import com.example.edubridge.filestream.FileManager
import kotlinx.coroutines.launch
import androidx.core.content.FileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Obtenemos el applicationId para el FileProvider
    val applicationId = context.packageName

    // Inicialización de gestores de archivos y descarga
    val fileManager = remember { FileManager(context) }
    val downloadManager = remember { DownloadManager(context) }

    Column(modifier = modifier.fillMaxSize()) {
        // Barra de Búsqueda
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("Buscar por título o autor...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true
        )

        // Lista de Recursos Filtrados
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.filteredResources, key = { it.id }) { resource ->
                ResourceCard(
                    resource = resource,
                    onClick = {
                        scope.launch {

                            // Genera nombre de archivo único
                            val filename = fileManager.generateFilename(resource.title, resource.id)

                            // Descarga el archivo (o cargarlo si ya existe)
                            val localFile = downloadManager.downloadFile(resource.url, filename)

                            if (localFile != null) {
                                // Registra la vista en MySQL (Web Service DML)
                                viewModel.logResourceView(resource.id, "ALUMNO_MATRICULA_MOCK")

                                try {
                                    // Generar URI segura (requiere la configuración del provider en el Manifest)
                                    val uri: Uri = FileProvider.getUriForFile(
                                        context,
                                        "$applicationId.fileprovider", // Debe coincidir con el 'authorities' del Manifest
                                        localFile
                                    )
                                    val openIntent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(openIntent)
                                } catch (e: Exception) {
                                    Log.e("Library", "Error al intentar abrir archivo local: ${e.message}")
                                }

                            } else {
                                // Si falla la descarga, intenta abrir la URL externa como fallback
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resource.url))
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    Log.e("Library", "Fallo al abrir archivo local y URL externa.")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}


//Tarjeta para mostrar cada recurso de la biblioteca.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceCard(
    resource: Resource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Autor: ${resource.author}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}