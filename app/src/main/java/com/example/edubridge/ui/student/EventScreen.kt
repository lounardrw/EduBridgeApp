package com.example.edubridge.ui.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.edubridge.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * La firma de la función ahora recibe el ViewModel
 * para acceder directamente al estado unificado.
 */
@Composable
fun EventsScreen(modifier: Modifier = Modifier, viewModel: EventViewModel = viewModel()) {
    // ESTADOS
    // Ahora obtenemos el estado de la UI desde el ViewModel
    val uiState by viewModel.uiState.collectAsState()
    var selectedEvent by remember { mutableStateOf<EventModuleUI?>(null) }

    //LÓGICA DE FILTRADO
    val filteredEvents = remember(uiState.events, uiState.currentFilter) {
        if (uiState.currentFilter == EventType.TODO) {
            uiState.events
        } else {
            uiState.events.filter { it.type == uiState.currentFilter }
        }
    }

    //DIÁLOGO DE DETALLE
    selectedEvent?.let { event ->
        EventDetailDialog(
            event = event,
            onDismiss = { selectedEvent = null }
        )
    }

    //ESTRUCTURA DE LA PANTALLA
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Título
        item {
            Text(
                text = "Eventos y Avisos",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            // Mostrar Error de Red
            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
            if (uiState.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
        }

        //Fila de Filtros
        item {
            FilterChips(
                selectedType = uiState.currentFilter,
                onFilterChange = { newFilter -> viewModel.filterEvents(newFilter) }
            )
        }

        //Lista de Eventos Filtrados
        if (filteredEvents.isEmpty() && !uiState.loading) {
            item {
                Text(
                    text = "No hay eventos en esta categoría.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(filteredEvents, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    onCardClick = { selectedEvent = event }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(selectedType: EventType, onFilterChange: (EventType) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(EventType.values()) { eventType ->
            FilterChip(
                selected = selectedType == eventType,
                onClick = { onFilterChange(eventType) },
                label = { Text(eventType.displayName) },
                leadingIcon = {
                    if (selectedType == eventType) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }
            )
        }
    }
}


@Composable
fun EventCard(event: EventModuleUI, onCardClick: () -> Unit) {
    val imageResId = R.drawable.ic_launcher_foreground

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Cartel del evento: ${event.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(event.categoryColor))

                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Formato de fecha
                    Text(text = event.date.substringAfterLast('-'), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = event.categoryColor)
                    Text(text = event.date.substringBeforeLast('-'), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                }

                Column(
                    modifier = Modifier.padding(vertical = 12.dp).padding(end = 12.dp).weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = event.description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray, lineHeight = 16.sp, maxLines = 2)
                }
            }
        }
    }
}

@Composable
fun EventDetailDialog(event: EventModuleUI, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                val imageResId = R.drawable.ic_launcher_foreground

                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = event.longDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
