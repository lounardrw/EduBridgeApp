package com.example.edubridge.ui.student

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edubridge.ui.LibraryViewModel
import com.example.edubridge.ui.LibraryModuleUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(modifier: Modifier = Modifier, viewModel: LibraryViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    /**
     * Lógica para abrir recursos (Links o Archivos Locales)
     */
    val onResourceClick = { resource: LibraryModuleUI ->
        try {
            val url = resource.url
            val uri = Uri.parse(url)
            val intent = if (url.startsWith("http")) {
                Intent(Intent.ACTION_VIEW, uri)
            } else {
                val mimeType = try { context.contentResolver.getType(uri) } catch (e: Exception) { null } ?: "*/*"
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
            context.startActivity(Intent.createChooser(intent, "Abrir con..."))
        } catch (e: Exception) {
            // Error al abrir recurso
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. TÍTULO DE LA SECCIÓN
        item {
            Text(
                text = "Biblioteca Digital",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Encuentra libros, guías y artículos científicos",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // 2. BUSCADOR (Punto Recuperado)
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar por título o autor...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF2E7D32)) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        }

        // 3. LISTA FILTRADA
        if (uiState.filteredResources.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                        Spacer(Modifier.height(8.dp))
                        Text("No se encontraron resultados", color = Color.Gray)
                    }
                }
            }
        } else {
            items(uiState.filteredResources, key = { it.id }) { book ->
                LibraryResourceCard(book = book, onClick = { onResourceClick(book) })
            }
        }
    }
}

@Composable
fun LibraryResourceCard(book: LibraryModuleUI, onClick: () -> Unit) {
    val context = LocalContext.current

    // Identificación visual del tipo de archivo
    val fileInfo = remember(book.url) {
        val uri = Uri.parse(book.url)
        val mimeType = try { context.contentResolver.getType(uri) } catch (e: Exception) { null } ?: ""

        when {
            book.url.startsWith("http") -> "WEB"
            mimeType == "application/pdf" || book.url.contains(".pdf") -> "PDF"
            mimeType.contains("word") || book.url.contains(".doc") -> "WORD"
            mimeType.contains("excel") || mimeType.contains("sheet") || book.url.contains(".xls") -> "EXCEL"
            mimeType.contains("powerpoint") || book.url.contains(".ppt") -> "POWERPOINT"
            else -> "ARCHIVO"
        }
    }

    val themeColor = when(fileInfo) {
        "PDF" -> Color(0xFFD32F2F)
        "WORD" -> Color(0xFF1976D2)
        "EXCEL" -> Color(0xFF388E3C)
        "POWERPOINT" -> Color(0xFFF57C00)
        "WEB" -> Color(0xFF0288D1)
        else -> Color(0xFF607D8B)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo colorido
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(themeColor.copy(alpha = 0.7f), themeColor)),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(fileInfo) {
                        "WEB" -> Icons.Default.Language
                        "PDF" -> Icons.Default.PictureAsPdf
                        else -> Icons.Default.Description
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Autor: ${book.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFE0E0E0),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}