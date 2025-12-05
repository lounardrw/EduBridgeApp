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
<<<<<<< HEAD
import androidx.compose.ui.Alignment
=======
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // Para manejar la navegación entre pantallas.
import com.example.edubridge.data.PanicAlertRepository // Lógica para enviar alertas de pánico.
import com.google.android.gms.location.LocationServices // Para obtener la ubicación GPS.
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch // Para tareas asíncronas (ej. abrir el menú).

<<<<<<< HEAD
// IMPORTACIÓN DE PANTALLAS
import com.example.edubridge.ui.student.ClassroomsScreen // Pantalla de Aulas (la única que tiene la lógica de navegación).

// DEFINICIONES DE PANTALLAS Y ESTRUCTURAS
// Define cada ítem que aparecerá en la barra de navegación inferior.
=======
// --- DEFINICIONES DE PANTALLAS Y NAVEGACIÓN INTERNA ---
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
data class NavItem(val label: String, val icon: ImageVector, val screen: Screen)

// Define las "rutas" internas de la pantalla principal (las pestañas).
sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Events : Screen("events")
    object Classrooms : Screen("classrooms")
}

<<<<<<< HEAD
// Stubs TEMPORALES y LOCALES
// Se usan hasta que los compañeros implementen las funciones Composable reales.
@Composable
fun EventsScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("Eventos y Avisos (Montse) - STUB LOCAL", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("Biblioteca Digital (Isaac) - STUB LOCAL", style = MaterialTheme.typography.titleLarge)
    }
}
=======
// --- PANTALLAS PROVISIONALES (PARA EVENTOS Y AULAS) ---
@Composable fun ClassroomsScreen(modifier: Modifier = Modifier) { Text("Aulas Interactivas (Cuenca)", modifier) }
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376


// ==================================================================
// PANTALLA PRINCIPAL DEL ALUMNO (Home)
<<<<<<< HEAD
@Composable
fun StudentHomeScreen() {
    // Llama a la implementación principal, usando valores por defecto.
    StudentHomeScreen(email = "temporal@edubridge.com", navController = null)
}
@Composable
fun StudentHomeScreen(navController: NavController) {
    // Llama a la implementación principal, pasando el NavController real.
    StudentHomeScreen(email = "temporal@edubridge.com", navController = navController)
}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
// Implementación principal de la pantalla del alumno.
fun StudentHomeScreen(email: String, navController: NavController?) {
    val context = LocalContext.current // Acceso al contexto de Android.
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope() // Se usa para lanzar tareas asíncronas (ej. abrir el menú).

    // Bandera para saber si podemos navegar (si NavController no es nulo).
    val isNavAvailable = navController != null

    // Estados de la UI
    val profileDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Menú lateral (Perfil).
    var showSettingsSheet by remember { mutableStateOf(false) } // Panel de ajustes inferior.

    // Definición de la barra de navegación inferior.
=======
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
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
    val navigationItems = listOf(
        NavItem(label = "Biblioteca", icon = Icons.Default.MenuBook, screen = Screen.Library),
        NavItem(label = "Eventos", icon = Icons.Default.Event, screen = Screen.Events),
        NavItem(label = "Aulas", icon = Icons.Default.School, screen = Screen.Classrooms)
    )
<<<<<<< HEAD
    var currentScreen: Screen by remember { mutableStateOf<Screen>(Screen.Library) } // Pestaña activa.


    // Mecanismo para solicitar permisos de GPS.
=======

    // Mecanismo para solicitar permisos de ubicación (GPS)
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
<<<<<<< HEAD
            // Permiso OK: Intentar obtener la ubicación.
=======
            // Permiso otorgado, obtener ubicación
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.d("PanicButton", "Ubicación obtenida: $latLng")
                    // Envía la alerta al repositorio (y de ahí al servidor Node.js).
                    PanicAlertRepository.triggerAlert(email, latLng)
                    Toast.makeText(context, "¡Alerta enviada!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
<<<<<<< HEAD
            // Permiso denegado.
=======
            // Permiso denegado
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
            Toast.makeText(context, "El permiso de ubicación es necesario para el botón de pánico.", Toast.LENGTH_LONG).show()
        }
    }

<<<<<<< HEAD
    // CONTENEDOR PRINCIPAL: Implementa el menú lateral izquierdo (Perfil).
    ModalNavigationDrawer(
        drawerContent = { StudentProfileDrawerContent(profileDrawerState, email = email) },
        drawerState = profileDrawerState,
        gesturesEnabled = true
    ) {
        // SCAFFOLD: Estructura base de la aplicación.
=======
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
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Portal del Alumno") },
<<<<<<< HEAD
                    // Botón para abrir el Menú de Perfil.
=======
                    // Icono para abrir el menú de perfil (izquierdo)
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { profileDrawerState.open() } }) {
                            Icon(Icons.Default.Person, contentDescription = "Menú Perfil")
                        }
                    },
                    // Iconos de acción a la derecha
                    actions = {
<<<<<<< HEAD
                        // Botón de IA (Asistente) - Tarea de Karen.
                        IconButton(onClick = { /* TODO: Lógica para abrir el chat de IA */ }) {
                            Icon(Icons.Default.SmartToy, contentDescription = "Asistente IA")
                        }
                        // Botón de Ajustes (abre el panel flotante inferior).
=======
                        // Icono para abrir la ficha de configuración (derecho)
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Menú Ajustes")
                        }
                    }
                )
            },
            bottomBar = {
<<<<<<< HEAD
                // Barra de Navegación Inferior (pestañas).
=======
                // Barra de navegación inferior
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
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
<<<<<<< HEAD
                // BOTÓN DE PÁNICO (FAB): Grande, rojo y visible para acción crítica.
=======
                // Botón de pánico
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
                FloatingActionButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(72.dp).padding(8.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Botón de Pánico", modifier = Modifier.size(40.dp))
                }
            }
        ) { innerPadding ->
<<<<<<< HEAD
            // CONTENIDO CENTRAL: Muestra la pantalla seleccionada (Tab Content).
=======
            // Contenido central que cambia según la selección de la barra inferior
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
            val modifier = Modifier.padding(innerPadding)
            when (currentScreen) {
                // ¡Asegúrate de que estas llamadas usan tus Composables reales!
                is Screen.Library -> LibraryScreen(modifier = modifier)
                is Screen.Events -> EventsScreen(modifier = modifier)
                // Llama a la pantalla Aulas si la navegación está disponible.
                is Screen.Classrooms -> if (isNavAvailable) {
                    ClassroomsScreen(
                        modifier = modifier,
                        navController = navController!! // Uso seguro de NavController
                    )
                } else {
                    // Muestra un mensaje de error si la navegación falla (caso de prueba/error).
                    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aulas: Navegación no disponible", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

<<<<<<< HEAD
    // PANEL FLOTANTE INFERIOR: Menú de Ajustes.
=======
    // Ficha modal que se muestra cuando `showSettingsSheet` es verdadero
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
    if (showSettingsSheet) {
        SettingsModalSheet(onDismiss = { showSettingsSheet = false })
    }
}

<<<<<<< HEAD
// COMPOSABLES AUXILIARES (adelanto)
// Contenido del menú lateral (Drawer) de perfil.
@Composable
fun StudentProfileDrawerContent(drawerState: DrawerState, email: String) {
    val scope = rememberCoroutineScope() // Scope para cerrar el menú asíncronamente.

    ModalDrawerSheet(
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Perfil del Alumno", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Muestra la información del usuario (Email).
            Text("Correo:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(email, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { scope.launch { drawerState.close() } }, // Cierra el menú.
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar Contraseña (Demo)")
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp)) // Línea separadora.

            Text(
                "Promedio: N/A",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
=======

// ==================================================================
// COMPOSABLES AUXILIARES (Menús de Karen)
// ==================================================================

@Composable
fun StudentProfileDrawerContent(drawerState: DrawerState, email: String) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
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
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
    }
}

// Panel inferior de ajustes (ModalBottomSheet).
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
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

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

// Tarjeta reutilizable para los ítems de ajuste.
@Composable
fun SettingItem(title: String, description: String) {
<<<<<<< HEAD
    Card(Modifier.fillMaxWidth().clickable { /* Lógica de clic */ }) {
=======
    Card(Modifier.fillMaxWidth().clickable { /* TODO: Lógica para abrir contenido detallado */ }) {
>>>>>>> 060a8501f6031e602b26a7a5430dd4b7b095e376
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
