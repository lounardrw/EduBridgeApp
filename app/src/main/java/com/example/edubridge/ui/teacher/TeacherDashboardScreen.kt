package com.example.edubridge.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edubridge.data.PanicAlertRepository
import com.example.edubridge.data.SessionManager

data class ManagementOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    onManageLibrary: () -> Unit,
    onManageEvents: () -> Unit,
    onManageQuizzes: () -> Unit,
    onViewAlert: () -> Unit,
    onLogout: () -> Unit // Callback para logout
) {
    val activeAlert by PanicAlertRepository.activeAlert.collectAsState(initial = null)
    val primaryGreen = Color(0xFF2E7D32)

    val options = listOf(
        ManagementOption("Biblioteca", "Gestionar libros y PDFs", Icons.Default.MenuBook, Color(0xFF1976D2), onManageLibrary),
        ManagementOption("Anuncios", "Publicar eventos y avisos", Icons.Default.Campaign, Color(0xFFF57C00), onManageEvents),
        ManagementOption("Cuestionarios", "Gestionar módulos de grado", Icons.Default.Assignment, Color(0xFF7B1FA2), onManageQuizzes)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("PANEL DEL PROFESOR", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                },
                actions = {
                    // BOTÓN DE SALIDA FUNCIONAL
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, "Cerrar Sesión", tint = Color.Red, modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = primaryGreen
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize().background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Centro de Gestión",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text("Administra el contenido de la plataforma", color = Color.Gray)
            }

            if (activeAlert != null) {
                item {
                    TeacherAlertCard(onClick = onViewAlert)
                }
            }

            items(options) { option ->
                TeacherManagementCard(option)
            }
        }
    }
}

@Composable
fun TeacherManagementCard(option: ManagementOption) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp).clickable { option.onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = option.color.copy(alpha = 0.1f), modifier = Modifier.size(56.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(option.icon, null, tint = option.color, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(Modifier.width(20.dp))
            Column(Modifier.weight(1f)) {
                Text(option.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(option.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun TeacherAlertCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text("¡ALERTA ACTIVA!", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("Un alumno requiere asistencia inmediata.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}