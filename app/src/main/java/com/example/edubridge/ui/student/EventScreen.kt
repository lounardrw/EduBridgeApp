package com.example.edubridge.ui.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edubridge.R

@Composable
fun EventsScreen(modifier: Modifier) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Eventos y Avisos",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(3) { index ->
            when (index) {
                0 -> EventCard(
                    title = "Entrega de Calificaciones",
                    description = "Reunión general en el auditorio para la entrega del primer parcial.",
                    date = "25 ENE",
                    categoryColor = Color(0xFFFFEB3B), // Amarillo
                    // Nombre de la imagen 1
                    imageResId = R.drawable.evento_calificaciones
                )
                1 -> EventCard(
                    title = "Torneo de Fútbol Interescolar",
                    description = "¡Apoya a nuestro equipo en la gran final! No faltes.",
                    date = "28 ENE",
                    categoryColor = Color(0xFFF44336), // Rojo
                    // Nombre de la imagen 2
                    imageResId = R.drawable.evento_futbol
                )
                else -> EventCard(
                    title = "Mantenimiento de Plataforma",
                    description = "El sistema estará fuera de línea de 10 PM a 11 PM.",
                    date = "30 ENE",
                    categoryColor = Color(0xFF169600), // Verde
                    // Nombre de la imagen 3
                    imageResId = R.drawable.evento_mantenimiento
                )
            }
        }
    }
}

@Composable
fun EventCard(
    title: String,
    description: String,
    date: String,
    categoryColor: Color,
    imageResId: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Cartel del evento: $title",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min) // Adapta la altura al contenido
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(8.dp)
                        .background(categoryColor)
                )


                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = date.substringBefore(" "),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = categoryColor
                    )
                    Text(
                        text = date.substringAfter(" "),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }

                Column(
                    modifier = Modifier.padding(vertical = 16.dp).padding(end = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EventPlaceholderCard() {
}



