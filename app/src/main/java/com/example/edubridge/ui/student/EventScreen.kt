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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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

// ----- ENUM PARA LOS FILTROS -----
enum class EventType(val displayName: String) {
    TODO("Todo"),
    PRÓXIMOS("Próximos"),
    BECAS("Becas"),
    AVISOS("Avisos")
}

@Composable
fun EventsScreen(
    modifier: Modifier = Modifier,
    events: List<EventData> //  AHORA LA LISTA VIENE DE FUERA
) {
    var selectedEvent by remember { mutableStateOf<EventData?>(null) }
    var currentFilter by remember { mutableStateOf(EventType.TODO) }

    val filteredEvents = remember(currentFilter, events) {
        if (currentFilter == EventType.TODO) {
            events
        } else {
            events.filter { it.type == currentFilter }
        }
    }

    selectedEvent?.let { event ->
        EventDetailDialog(
            event = event,
            onDismiss = { selectedEvent = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Eventos y Avisos",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            FilterChips(
                selectedType = currentFilter,
                onFilterChange = { currentFilter = it }
            )
        }

        if (filteredEvents.isEmpty()) {
            item {
                Text(
                    text = "No hay eventos en esta categoría.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            items(filteredEvents) { event ->
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
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(EventType.values()) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onFilterChange(type) },
                label = { Text(type.displayName) },
                leadingIcon = {
                    if (selectedType == type) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )
        }
    }
}

@Composable
fun EventCard(event: EventData, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = event.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(event.categoryColor)
                )

                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun EventDetailDialog(event: EventData, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Image(
                    painter = painterResource(id = event.imageResId),
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
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = event.longDescription)
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

// ----- MODELO DE UI -----
data class EventData(
    val title: String,
    val description: String,
    val longDescription: String,
    val date: String,
    val categoryColor: Color,
    val imageResId: Int,
    val type: EventType
)
