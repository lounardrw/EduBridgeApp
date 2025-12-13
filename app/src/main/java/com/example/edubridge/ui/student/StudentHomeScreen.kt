package com.example.edubridge.ui.student

import com.example.edubridge.ui.student.EventsScreen
import com.example.edubridge.ui.student.LibraryScreen
import com.example.edubridge.ui.student.ClassroomsScreen
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.edubridge.data.PanicAlertRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

// --- DEFINICIONES DE PANTALLAS Y NAVEGACIÓN INTERNA (Versión de 'main') ---
data class NavItem(val label: String, val icon: ImageVector, val screen: Screen)

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Events : Screen("events")
    object Classrooms : Screen("classrooms")
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun StudentHomeScreen(email: String, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val profileDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showSettingsSheet by remember { mutableStateOf(false) }
    var currentScreen: Screen by remember { mutableStateOf<Screen>(Screen.Library) }

    val navigationItems = listOf(
        NavItem(label = "Biblioteca", icon = Icons.Default.MenuBook, screen = Screen.Library),
        NavItem(label = "Eventos", icon = Icons.Default.Event, screen = Screen.Events),
        NavItem(label = "Aulas", icon = Icons.Default.School, screen = Screen.Classrooms)
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.d("PanicButton", "Ubicación obtenida: $latLng")
                    PanicAlertRepository.triggerAlert(email, latLng) // Usamos el email real
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


    ModalNavigationDrawer(
        drawerState = profileDrawerState,
        gesturesEnabled = profileDrawerState.isOpen,
        drawerContent = {
            StudentProfileDrawerContent(
                drawerState = profileDrawerState,
                email = email
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Portal del Alumno") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { profileDrawerState.open() } }) {
                            Icon(Icons.Default.Person, contentDescription = "Menú Perfil")
                        }
                    },
                    actions = {
                        // Mantenemos el botón de Asistente de IA de la rama de Karen
                        IconButton(onClick = { /* TODO: Lógica para abrir el chat de IA */ }) {
                            Icon(Icons.Default.SmartToy, contentDescription = "Asistente IA")
                        }
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Menú Ajustes")
                        }
                    }
                )
            },
            bottomBar = {
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
                // Botón de pánico (Usamos la versión mejorada de la rama de Karen)
                FloatingActionButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(72.dp)
                        .padding(8.dp), // Estilo de Karen
                    shape = MaterialTheme.shapes.extraLarge      // Estilo de Karen
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Botón de Pánico",
                        modifier = Modifier.size(40.dp) // Estilo de Karen
                    )
                }
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (currentScreen) {
                // Las llamadas a las pantallas reales, como en 'main'
                is Screen.Library -> LibraryScreen(modifier = modifier)
                is Screen.Events -> EventsScreen(modifier = modifier)
                // Pasa el navController a la pantalla de Aulas
                is Screen.Classrooms -> ClassroomsScreen(
                    modifier = modifier,
                    navController = navController // <-- PÁSALA AQUÍ
                )
            }
        }
    }

    // Ficha modal que se muestra cuando `showSettingsSheet` es verdadero
    if (showSettingsSheet) {
        SettingsModalSheet(onDismiss = { showSettingsSheet = false })
    }
}

@Composable
fun StudentProfileDrawerContent(
    drawerState: DrawerState,
    email: String,
    modifier: Modifier = Modifier
) {    val scope = rememberCoroutineScope()
    // ENVUELVE TODO EN UN MODAL DRAWERSHEET
    // Este componente ya tiene el color de fondo y la elevación correctos
    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModalSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Opciones y Fichas",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center // Centramos el título como en la rama de Karen
                )
            }
            item { SettingItem(title = "Términos de Uso", description = "Reglas y Acuerdos Legales.") }
            item { SettingItem(title = "Aviso de Privacidad", description = "Tratamiento y uso exclusivo de datos de ubicación.") }
            item { SettingItem(title = "Contacto", description = "Teléfonos y horarios escolares.") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Button(onClick = onDismiss, modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun SettingItem(title: String, description: String) {
    Card(Modifier
        .fillMaxWidth()
        .clickable { /* TODO: Lógica para abrir contenido detallado */ }) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
