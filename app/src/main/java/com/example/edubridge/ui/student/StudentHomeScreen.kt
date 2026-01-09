package com.example.edubridge.ui.student

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import com.example.edubridge.data.PanicAlertRepository
import com.example.edubridge.data.SessionManager
import com.example.edubridge.ui.LibraryViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Library : Screen("library", "Biblioteca", Icons.Default.MenuBook)
    object Events : Screen("events", "Eventos", Icons.Default.EventNote)
    object Classrooms : Screen("classrooms", "Aulas", Icons.Default.School)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun StudentHomeScreen(email: String, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val libraryViewModel: LibraryViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()

    var showSettingsSheet by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Library) }

    val primaryGreen = Color(0xFF2E7D32)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    PanicAlertRepository.triggerAlert(email, LatLng(it.latitude, it.longitude), "USER_ID")
                    Toast.makeText(context, "¡ALERTA ENVIADA!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            StudentProfileDrawerContent(email = email, sessionManager = sessionManager, onLogout = {
                sessionManager.clear()
                navController.navigate("login") { popUpTo(0) }
            })
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Portal del Alumno",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", modifier = Modifier.size(30.dp))
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            sessionManager.clear()
                            navController.navigate("login") { popUpTo(0) }
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Salir", tint = Color.Red)
                        }
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                        }
                    },
                    // FIX: Se usa TopAppBarDefaults en lugar de CenterAlignedTopAppBarDefaults
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = primaryGreen
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    val items = listOf(Screen.Library, Screen.Events, Screen.Classrooms)
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentScreen == item,
                            onClick = { currentScreen = item },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.label, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = primaryGreen,
                                selectedTextColor = primaryGreen,
                                indicatorColor = Color(0xFFE8F5E9)
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) },
                    containerColor = Color.Red,
                    shape = CircleShape,
                    modifier = Modifier.size(70.dp).padding(4.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "SOS", tint = Color.White, modifier = Modifier.size(35.dp))
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FA))) {
                when (currentScreen) {
                    Screen.Library -> LibraryScreen(viewModel = libraryViewModel)
                    Screen.Events -> StudentEventsRoute(viewModel = eventViewModel)
                    Screen.Classrooms -> ClassroomsScreen(navController = navController)
                }
            }
        }
    }

    if (showSettingsSheet) {
        SettingsModalSheet(onDismiss = { showSettingsSheet = false })
    }
}

@Composable
fun StudentProfileDrawerContent(email: String, sessionManager: SessionManager, onLogout: () -> Unit) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Box(
                modifier = Modifier.size(80.dp).background(Color(0xFF2E7D32), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text("¡Hola de nuevo!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(email, color = Color.Gray)

            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Promedio General", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Text("Actualizado recientemente", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("9.4", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModalSheet(onDismiss: () -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        if (selectedOption == null) {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                Text(
                    "Ajustes y Legal",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                SettingItem("Términos de Uso", "Condiciones de servicio EduBridge", Icons.Default.Gavel) {
                    selectedOption = "Terminos"
                }
                SettingItem("Aviso de Privacidad", "Cómo protegemos tus datos", Icons.Default.Security) {
                    selectedOption = "Privacidad"
                }
                SettingItem("Contacto", "Soporte técnico y académico", Icons.Default.ContactSupport) {
                    selectedOption = "Contacto"
                }
            }
        } else {
            Column(modifier = Modifier.padding(24.dp).padding(bottom = 48.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { selectedOption = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                    Text(
                        text = when(selectedOption) {
                            "Terminos" -> "Términos de Uso"
                            "Privacidad" -> "Aviso de Privacidad"
                            "Contacto" -> "Contacto de Soporte"
                            else -> ""
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(Modifier.height(16.dp))

                val content = when(selectedOption) {
                    "Terminos" -> "EduBridge es una plataforma educativa integral. Al utilizar esta aplicación, el usuario acepta que el contenido es para uso exclusivamente académico. Queda prohibida la reproducción parcial o total de materiales sin autorización. La institución se reserva el derecho de dar de baja cuentas que hagan uso indebido del botón de pánico."
                    "Privacidad" -> "Tus datos personales, académicos y de ubicación están protegidos bajo protocolos de encriptación. La ubicación GPS solo se transmite de manera activa cuando se presiona el botón de pánico (SOS), con el único fin de proveer asistencia inmediata por parte de seguridad institucional."
                    "Contacto" -> "Para soporte técnico, fallas en la plataforma o dudas académicas:\n\n• Teléfono: 5629051665\n• Correo: luis_ce1@tesch.edu.mx\n• Horario: Lunes a Viernes (8:00 AM - 6:00 PM)\n• Ubicación: Tecnológico de Estudios Superiores de Chalco."
                    else -> ""
                }

                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun SettingItem(title: String, desc: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF2E7D32))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}