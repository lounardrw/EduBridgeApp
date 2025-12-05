package com.example.edubridge.ui.student

import com.example.edubridge.ui.student.EventsScreen
import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.edubridge.data.PanicAlertRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

// --- DEFINICIONES DE PANTALLAS Y NAVEGACIÓN INTERNA ---
data class NavItem(val label: String, val icon: ImageVector, val screen: Screen)

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Events : Screen("events")
    object Classrooms : Screen("classrooms")
}

// ==================================================================
// PANTALLA PRINCIPAL DEL ALUMNO (Home)
// ==================================================================

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun StudentHomeScreen(email: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // --- ESTADOS DE LA UI ---
    // Para el menú lateral izquierdo (Perfil)
    val profileDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Para la ficha inferior derecha (Configuración)
    var showSettingsSheet by remember { mutableStateOf(false) }
    // Para la navegación inferior (Biblioteca, Eventos, Aulas)
    var currentScreen: Screen by remember { mutableStateOf<Screen>(Screen.Library) }

    // Lista de ítems para la barra de navegación inferior
    val navigationItems = listOf(
        NavItem(label = "Biblioteca", icon = Icons.Default.MenuBook, screen = Screen.Library),
        NavItem(label = "Eventos", icon = Icons.Default.Event, screen = Screen.Events),
        NavItem(label = "Aulas", icon = Icons.Default.School, screen = Screen.Classrooms)
    )

    // Mecanismo para solicitar permisos de ubicación (GPS)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            // Permiso otorgado, obtener ubicación
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.d("PanicButton", "Ubicación obtenida: $latLng")
                    PanicAlertRepository.triggerAlert("Alumno de Prueba", latLng)
                    Toast.makeText(context, "¡Alerta enviada!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Permiso denegado
            Toast.makeText(context, "El permiso de ubicación es necesario para el botón de pánico.", Toast.LENGTH_LONG).show()
        }
    }

    // --- ESTRUCTURA PRINCIPAL DE LA UI ---
    ModalNavigationDrawer(
        drawerState = profileDrawerState,
        gesturesEnabled = profileDrawerState.isOpen,
        drawerContent = {StudentProfileDrawerContent(
            drawerState = profileDrawerState, // Correct name
            email = email
        )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Portal del Alumno") },
                    // Icono para abrir el menú de perfil (izquierdo)
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { profileDrawerState.open() } }) {
                            Icon(Icons.Default.Person, contentDescription = "Menú Perfil")
                        }
                    },
                    // Iconos de acción a la derecha
                    actions = {
                        // Icono para abrir la ficha de configuración (derecho)
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Menú Ajustes")
                        }
                    }
                )
            },
            bottomBar = {
                // Barra de navegación inferior
                NavigationBar {
                    navigationItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentScreen == item.screen,
                            onClick = { currentScreen = item.screen },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            },
            floatingActionButton = {
                // Botón de pánico
                FloatingActionButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Botón de Pánico")
                }
            }
        ) { innerPadding ->
            // Contenido central que cambia según la selección de la barra inferior
            val modifier = Modifier.padding(innerPadding)
            when (currentScreen) {
                // ¡Asegúrate de que estas llamadas usan tus Composables reales!
                is Screen.Library -> LibraryScreen(modifier = modifier)
                is Screen.Events -> EventsScreen(modifier = modifier)
                is Screen.Classrooms -> ClassroomsScreen(modifier = modifier)
            }
        }
    }

    // Ficha modal que se muestra cuando `showSettingsSheet` es verdadero
    if (showSettingsSheet) {
        SettingsModalSheet(onDismiss = { showSettingsSheet = false })
    }
}


// ==================================================================
// COMPOSABLES AUXILIARES (Menús de Karen)
// ==================================================================

@Composable
fun StudentProfileDrawerContent(
    drawerState: DrawerState,
    email: String,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize() 
    ) {
        Text("Perfil del Alumno", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Correo: $email", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Promedio: N/A", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch { drawerState.close() }
            // TODO: Lógica para abrir el diálogo de cambio de contraseña
        }) {
            Text("Actualizar Contraseña (Demo)")
        }
        Divider(Modifier.padding(vertical = 16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModalSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Opciones y Fichas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp)) }

            item { SettingItem(title = "Términos de Uso", description = "Reglas y Acuerdos Legales.") }
            item { SettingItem(title = "Aviso de Privacidad", description = "Tratamiento y uso exclusivo de datos de ubicación.") }
            item { SettingItem(title = "Contacto", description = "Teléfonos y horarios escolares.") }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun SettingItem(title: String, description: String) {
    Card(Modifier.fillMaxWidth().clickable { /* TODO: Lógica para abrir contenido detallado */ }) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
