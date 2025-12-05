package com.example.edubridge.ui.student

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
import androidx.compose.ui.unit.dp
import com.example.edubridge.data.PanicAlertRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

// DEFINICIONES DE PANTALLAS
data class NavItem(val label: String, val icon: ImageVector, val screen: Screen)

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Events : Screen("events")
    object Classrooms : Screen("classrooms")
}

// Stubs (Implementación Provisional/Simulada)
@Composable fun LibraryScreen(modifier: Modifier = Modifier) { Text("Biblioteca Digital (Isaac)", modifier) }
@Composable fun EventsScreen(modifier: Modifier = Modifier) { Text("Eventos y Avisos (Montse)", modifier) }
@Composable fun ClassroomsScreen(modifier: Modifier = Modifier) { Text("Aulas Interactivas (Cuenca)", modifier) }

// PANTALLA PRINCIPAL DEL ALUMNO (Home)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
// MODIFICACIÓN PRINCIPAL: Acepta el email como parámetro
fun StudentHomeScreen(email: String) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    //Estados para los Menús Laterales y Fichas
    val profileDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Menú Izquierdo (Perfil)
    var showSettingsSheet by remember { mutableStateOf(false) } // Menú Derecho (Configuración/Fichas)

    val navigationItems = listOf(
        NavItem(label = "Biblioteca", icon = Icons.Default.MenuBook, screen = Screen.Library),
        NavItem(label = "Eventos", icon = Icons.Default.Event, screen = Screen.Events),
        NavItem(label = "Aulas", icon = Icons.Default.School, screen = Screen.Classrooms)
    )
    var currentScreen: Screen by remember { mutableStateOf<Screen>(Screen.Library) }


    //Launcher (Mecanismo para solicitar Permisos) para solicitar GPS
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            // Permiso otorgado
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude) // LatLng (Latitud y Longitud GPS)
                    Log.d("PanicButton", "Ubicación obtenida: $latLng")
                    PanicAlertRepository.triggerAlert("Alumno de Prueba", latLng)
                    Toast.makeText(context, "¡Alerta enviada!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(
                context,
                "El permiso de ubicación es necesario para el botón de pánico.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // INICIO DEL CONTENEDOR LATERAL IZQUIERDO (PERFIL) (ModalNavigationDrawer)
    ModalNavigationDrawer(
        //Pasa el email al contenido del Drawer
        drawerContent = { StudentProfileDrawerContent(profileDrawerState, email = email) },
        drawerState = profileDrawerState,
        gesturesEnabled = profileDrawerState.isOpen
    ) {
        // CUERPO PRINCIPAL (SCAFFOLD - Estructura de Diseño Principal)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Portal del Alumno") },
                    //Icono izquierdo para abrir el Perfil
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { profileDrawerState.open() } }) {
                            Icon(Icons.Default.Person, contentDescription = "Menú Perfil")
                        }
                    },
                    actions = {
                        //Icono derecho para abrir la Ficha de Configuración
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Menú Ajustes")
                        }
                    }
                )
            },
            bottomBar = {
                //Implementación de la Navegación Inferior
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
                //Botón de Pánico (FAB - Floating Action Button)
                FloatingActionButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Botón de Pánico")
                }
            }
        ) { innerPadding ->
            // Contenido principal que cambia según la pantalla seleccionada
            val modifier = Modifier.padding(innerPadding)
            when (currentScreen) {
                is Screen.Library -> LibraryScreen(modifier = modifier)
                is Screen.Events -> EventsScreen(modifier = modifier)
                is Screen.Classrooms -> ClassroomsScreen(modifier = modifier)
            }
        }
    } // FIN ModalNavigationDrawer

    //Ficha Modal (ModalBottomSheet - Panel Flotante Inferior) para el Menú Derecho
    if (showSettingsSheet) {
        SettingsModalSheet(onDismiss = { showSettingsSheet = false })
    }
}


// ------------------------------------------------------------------
// COMPOSABLES AUXILIARES DE KAREN (Avance)
// ------------------------------------------------------------------

@Composable
// MODIFICACIÓN CLAVE: Acepta el email como parámetro
fun StudentProfileDrawerContent(drawerState: DrawerState, email: String) {
    // KAREN: Contenido del Menú Izquierdo (Perfil)
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Perfil del Alumno", style = MaterialTheme.typography.headlineSmall)
        // Muestra el email real pasado desde el Login
        Text("Correo: $email", style = MaterialTheme.typography.bodyLarge)

        // MODIFICACIÓN DE KAREN: Botón para editar perfil (cumple con el requisito de "Actualizar Perfil")
        Button(onClick = {
            scope.launch { drawerState.close() }
            // Lógica para abrir el diálogo de cambio de contraseña
        }) {
            Text("Actualizar Contraseña (Demo)")
        }
        Divider(Modifier.padding(vertical = 8.dp))
        Text("Promedio: N/A", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModalSheet(onDismiss: () -> Unit) {
    // KAREN: Fichas Informativas para el Menú Derecho (Cumple con el requisito de "ventanas emergentes")
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Opciones y Fichas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp)) }

            // Simulación de Fichas (Términos, Privacidad, Contacto)
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
    Card(Modifier.fillMaxWidth().clickable { /* Clic para abrir el contenido */ }) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}