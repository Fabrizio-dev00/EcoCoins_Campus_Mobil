package com.ecocoins.campus.presentation.perfil

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.presentation.dashboard.DashboardViewModel
import com.ecocoins.campus.presentation.auth.AuthViewModel

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

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Animación de pulsación del avatar
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Mi Perfil",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar animado
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn()
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nombre del usuario
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it }
                    ) + fadeIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            user?.nombre ?: "Usuario",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    user?.correo ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Tarjeta de información
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it }
                    ) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Información Personal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            ProfileItem(
                                icon = Icons.Default.School,
                                label = "Carrera",
                                value = user?.carrera ?: "No especificado"
                            )

                            ProfileItem(
                                icon = Icons.Default.Badge,
                                label = "Rol",
                                value = user?.rol?.capitalize() ?: "Usuario"
                            )

                            Divider()

                            Text(
                                "Estadísticas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            ProfileItem(
                                icon = Icons.Default.Paid,
                                label = "EcoCoins",
                                value = "${user?.ecoCoins ?: 0}",
                                valueColor = MaterialTheme.colorScheme.tertiary
                            )

                            ProfileItem(
                                icon = Icons.Default.EmojiEvents,
                                label = "Nivel",
                                value = getNivelNombre(user?.nivel ?: 1)
                            )

                            ProfileItem(
                                icon = Icons.Default.Recycling,
                                label = "Total Reciclajes",
                                value = "${user?.totalReciclajes ?: 0}"
                            )

                            ProfileItem(
                                icon = Icons.Default.Scale,
                                label = "Kg Reciclados",
                                value = String.format("%.2f kg", user?.totalKgReciclados ?: 0.0)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de cerrar sesión
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it }
                    ) + fadeIn()
                ) {
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Logout, "Cerrar sesión")
                            Text(
                                "Cerrar Sesión",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Versión de la app
                Text(
                    "EcoCoinsCampus v1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Diálogo de confirmación de logout
    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                authViewModel.logout()
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }
}

@Composable
fun ProfileItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                "¿Cerrar sesión?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "¿Estás seguro de que deseas cerrar sesión? Deberás iniciar sesión nuevamente para acceder a tu cuenta.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar Sesión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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