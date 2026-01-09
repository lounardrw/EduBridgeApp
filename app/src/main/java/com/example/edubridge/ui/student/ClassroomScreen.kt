package com.example.edubridge.ui.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

val schoolGrades = listOf("1° Secundaria", "2° Secundaria", "3° Secundaria")

@Composable
fun ClassroomsScreen(modifier: Modifier = Modifier, navController: NavController) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // TÍTULO HOMOGÉNEO: Estandarizado con Eventos
            Text(
                text = "Grados Escolares",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black, // Consistente con Eventos
                color = Color.Black
            )
            Text(
                text = "Selecciona tu grado para ver módulos",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(Modifier.height(8.dp))
        }

        items(schoolGrades) { grade ->
            GradeCard(grade = grade) {
                navController.navigate("quiz_selection/$grade")
            }
        }
    }
}

@Composable
fun GradeCard(grade: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(110.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFE8F5E9), modifier = Modifier.size(50.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.School, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(grade, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}