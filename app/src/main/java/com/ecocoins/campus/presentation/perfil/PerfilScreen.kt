package com.ecocoins.campus.presentation.perfil

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.presentation.auth.AuthViewModel
import com.ecocoins.campus.presentation.dashboard.DashboardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Colores de EcoCoins Campus
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val EcoOrangeLight = Color(0xFFFFB74D)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val dashboardState by dashboardViewModel.uiState.collectAsState()
    val user = dashboardState.user
    val scope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PerfilTopBar(onNavigateBack = onNavigateBack)
        },
        containerColor = BackgroundLight
    ) { padding ->
        if (dashboardState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EcoGreenPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header con avatar y datos básicos
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        PerfilHeader(
                            nombre = user?.nombre ?: "Usuario",
                            email = user?.correo ?: "",
                            carrera = user?.carrera ?: ""
                        )
                    }
                }

                // Card de EcoCoins y nivel
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                    ) {
                        EcoCoinsLevelCard(
                            ecoCoins = user?.ecoCoins ?: 0,
                            nivel = user?.nivel ?: 1
                        )
                    }
                }

                // Estadísticas
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Mis Estadísticas",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )

                            StatsGrid(
                                totalReciclajes = user?.totalReciclajes ?: 0,
                                totalKg = user?.totalKgReciclados ?: 0.0
                            )
                        }
                    }
                }

                // Opciones del perfil
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Mi Cuenta",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )

                            PerfilOptionsCard(
                                onEditProfile = { /* TODO */ },
                                onHistorialReciclajes = { /* TODO: Navegar */ },
                                onMisRecompensas = { /* TODO: Navegar */ },
                                onConfiguracion = { /* TODO */ }
                            )
                        }
                    }
                }

                // Botón de cerrar sesión
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn()
                    ) {
                        LogoutButton(onClick = { showLogoutDialog = true })
                    }
                }

                // Espaciado al final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Diálogo de confirmación de logout
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    showLogoutDialog = false
                    scope.launch {
                        authViewModel.logout()
                        delay(300)
                        onLogout()
                    }
                },
                onDismiss = { showLogoutDialog = false }
            )
        }

        // Mensaje de error
        dashboardState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    action = {
                        TextButton(onClick = { dashboardViewModel.clearError() }) {
                            Text("OK", color = MaterialTheme.colorScheme.error)
                        }
                    }
                ) {
                    Text(error, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Mi Perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF212121)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun PerfilHeader(
    nombre: String,
    email: String,
    carrera: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar con iniciales
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(EcoGreenPrimary, EcoGreenLight)
                        )
                    )
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getInitials(nombre),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Nombre
            Text(
                text = nombre,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )

            // Email
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF757575)
                )
                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }

            // Carrera
            if (carrera.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF757575)
                    )
                    Text(
                        text = carrera,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }
    }
}

@Composable
fun EcoCoinsLevelCard(
    ecoCoins: Int,
    nivel: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // EcoCoins
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saldo Actual",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = null,
                            tint = EcoOrange,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "$ecoCoins",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "EcoCoins",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Divider(color = Color.White.copy(alpha = 0.3f))

            // Nivel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = EcoOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Nivel ${getNivelNombre(nivel)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Barra de progreso (simulada)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Progreso al siguiente nivel",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                LinearProgressIndicator(
                    progress = { getProgressToNextLevel(ecoCoins) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EcoOrange,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )
            }
        }
    }
}

@Composable
fun StatsGrid(
    totalReciclajes: Int,
    totalKg: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Reciclajes",
            value = "$totalReciclajes",
            icon = Icons.Default.Recycling,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Kg Reciclados",
            value = String.format("%.1f", totalKg),
            icon = Icons.Default.Scale,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "CO₂ Ahorrado",
            value = String.format("%.1f", totalKg * 2.5) + " kg",
            icon = Icons.Default.CloudDone,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Árboles",
            value = "${(totalKg / 10).toInt()}",
            icon = Icons.Default.Park,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = EcoGreenLight.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PerfilOptionsCard(
    onEditProfile: () -> Unit,
    onHistorialReciclajes: () -> Unit,
    onMisRecompensas: () -> Unit,
    onConfiguracion: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            PerfilOption(
                title = "Editar Perfil",
                icon = Icons.Default.Edit,
                onClick = onEditProfile,
                showDivider = true
            )
            PerfilOption(
                title = "Historial de Reciclajes",
                icon = Icons.Default.History,
                onClick = onHistorialReciclajes,
                showDivider = true
            )
            PerfilOption(
                title = "Mis Recompensas",
                icon = Icons.Default.CardGiftcard,
                onClick = onMisRecompensas,
                showDivider = true
            )
            PerfilOption(
                title = "Configuración",
                icon = Icons.Default.Settings,
                onClick = onConfiguracion,
                showDivider = false
            )
        }
    }
}

@Composable
fun PerfilOption(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press"
    )

    Column(modifier = Modifier.scale(scale)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isPressed = true
                    kotlinx.coroutines.GlobalScope.launch {
                        delay(100)
                        isPressed = false
                        onClick()
                    }
                }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = EcoGreenLight.copy(alpha = 0.2f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color(0xFF212121)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9E9E9E)
            )
        }

        if (showDivider) {
            Divider(
                color = Color(0xFFE0E0E0),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press"
    )

    Button(
        onClick = {
            isPressed = true
            kotlinx.coroutines.GlobalScope.launch {
                delay(100)
                isPressed = false
                onClick()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE53935),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Cerrar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "¿Cerrar sesión?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro que deseas cerrar sesión?",
                color = Color(0xFF757575)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Text("Cerrar Sesión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = EcoGreenPrimary)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

// Helper functions
fun getInitials(nombre: String): String {
    val parts = nombre.trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.isNotEmpty() -> parts[0].take(2).uppercase()
        else -> "?"
    }
}

fun getNivelNombre(nivel: Int): String {
    return when (nivel) {
        1 -> "Bronce 🥉"
        2 -> "Plata 🥈"
        3 -> "Oro 🥇"
        4 -> "Platino 💎"
        else -> "Bronce 🥉"
    }
}

fun getProgressToNextLevel(ecoCoins: Int): Float {
    val nextLevelCoins = ((ecoCoins / 1000) + 1) * 1000
    val currentLevelCoins = (ecoCoins / 1000) * 1000
    val progress = (ecoCoins - currentLevelCoins).toFloat() / (nextLevelCoins - currentLevelCoins)
    return progress.coerceIn(0f, 1f)
}