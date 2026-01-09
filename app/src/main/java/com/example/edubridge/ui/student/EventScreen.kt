package com.example.edubridge.ui.student

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

/**
 * Función auxiliar para detectar el tipo de archivo de forma unificada.
 */
@Composable
fun rememberFileType(imageUrl: String?): String {
    val context = LocalContext.current
    return remember(imageUrl) {
        val uri = Uri.parse(imageUrl ?: "")
        val mimeType = try { context.contentResolver.getType(uri) } catch (e: Exception) { null } ?: ""

        when {
            mimeType.startsWith("image/") -> "IMAGE"
            mimeType == "application/pdf" -> "PDF"
            mimeType.contains("word") || mimeType.contains("officedocument.word") -> "WORD"
            mimeType.contains("excel") || mimeType.contains("officedocument.spread") || mimeType.contains("sheet") -> "EXCEL"
            mimeType.contains("powerpoint") || mimeType.contains("officedocument.present") -> "POWERPOINT"
            else -> {
                val url = imageUrl?.lowercase() ?: ""
                if (url.endsWith(".pdf")) "PDF"
                else if (url.contains("document") || url.contains("file")) "DOCUMENTO"
                else "IMAGE"
            }
        }
    }
}

/**
 * Función para obtener el color temático según el tipo de archivo.
 */
fun getFileColor(fileType: String): Color {
    return when(fileType) {
        "PDF" -> Color(0xFFD32F2F)
        "WORD" -> Color(0xFF1976D2)
        "EXCEL" -> Color(0xFF388E3C)
        "POWERPOINT" -> Color(0xFFF57C00)
        else -> Color(0xFF455A64)
    }
}

@Composable
fun EventsScreen(modifier: Modifier = Modifier, viewModel: EventViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedEvent by remember { mutableStateOf<EventModuleUI?>(null) }

    val filteredEvents = remember(uiState.events, uiState.currentFilter) {
        if (uiState.currentFilter == EventType.TODO) uiState.events
        else uiState.events.filter { it.type == uiState.currentFilter }
    }

    selectedEvent?.let { event ->
        EventDetailDialog(event = event, onDismiss = { selectedEvent = null })
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Centro de Noticias", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
        }

        item {
            FilterChipsRow(selected = uiState.currentFilter, onSelected = { viewModel.filterEvents(it) })
        }

        items(filteredEvents, key = { it.id }) { event ->
            EventCard(event = event, onCardClick = { selectedEvent = event })
        }
    }
}

@Composable
fun EventDetailDialog(event: EventModuleUI, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var showZoomView by remember { mutableStateOf(false) }
    val fileInfo = rememberFileType(event.imageUrl)

    if (showZoomView && fileInfo == "IMAGE") {
        FullScreenZoomModal(url = event.imageUrl ?: "", onDismiss = { showZoomView = false })
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    if (fileInfo != "IMAGE") {
                        val fileColor = getFileColor(fileInfo)
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(fileColor.copy(0.8f), fileColor)))) {
                            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Icon(
                                    imageVector = if (fileInfo == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(90.dp),
                                    tint = Color.White
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(fileInfo, color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                            }
                        }
                    } else {
                        AsyncImage(
                            model = event.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clickable { showZoomView = true },
                            contentScale = ContentScale.Crop
                        )
                        Box(Modifier.align(Alignment.BottomEnd).padding(16.dp).background(Color.Black.copy(0.6f), CircleShape)) {
                            Icon(Icons.Default.ZoomIn, null, tint = Color.White, modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    Text(event.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Text(event.type.displayName, color = event.categoryColor, fontWeight = FontWeight.Bold)

                    HorizontalDivider(Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

                    Text(event.longDescription, style = MaterialTheme.typography.bodyLarge, lineHeight = 26.sp, color = Color.DarkGray)

                    Spacer(Modifier.height(32.dp))

                    if (fileInfo != "IMAGE" && event.imageUrl != null) {
                        Button(
                            onClick = {
                                try {
                                    val uri = Uri.parse(event.imageUrl)
                                    val mimeType = context.contentResolver.getType(uri) ?: "*/*"
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, mimeType)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Abrir con..."))
                                } catch (e: Exception) { }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.OpenInNew, null)
                            Spacer(Modifier.width(12.dp))
                            Text("ABRIR ARCHIVO", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("CERRAR", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenZoomModal(url: String, onDismiss: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += offsetChange
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AsyncImage(
                model = url, contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offset.x, translationY = offset.y)
                    .transformable(state = state),
                contentScale = ContentScale.Fit
            )
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(20.dp).background(Color.White.copy(0.2f), CircleShape)) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipsRow(selected: EventType, onSelected: (EventType) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(EventType.values()) { type ->
            FilterChip(selected = selected == type, onClick = { onSelected(type) }, label = { Text(type.displayName) })
        }
    }
}

@Composable
fun EventCard(event: EventModuleUI, onCardClick: () -> Unit) {
    val fileInfo = rememberFileType(event.imageUrl)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // CABECERA DE LA TARJETA (Imagen o Logo de Archivo)
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                if (fileInfo != "IMAGE") {
                    val fileColor = getFileColor(fileInfo)
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(fileColor.copy(0.8f), fileColor)))) {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(
                                imageVector = if (fileInfo == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = Color.White
                            )
                            Text(fileInfo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                } else {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // CUERPO DE LA TARJETA
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .drawBehind {
                        drawLine(color = event.categoryColor, start = Offset(0f, 0f), end = Offset(0f, size.height), strokeWidth = 4.dp.toPx())
                    }
            ) {
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(event.title, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    Text(event.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}