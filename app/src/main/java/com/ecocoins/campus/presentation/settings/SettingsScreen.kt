package com.ecocoins.campus.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.observeAsState("")
    val userEmail by viewModel.userEmail.observeAsState("")
    val notificationsEnabled by viewModel.notificationsEnabled.observeAsState(true)
    val darkModeEnabled by viewModel.darkModeEnabled.observeAsState(false)

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información de la cuenta
            item {
                Text(
                    text = "Cuenta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Usuario",
                                tint = EcoGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = userName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = userEmail,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Preferencias
            item {
                Text(
                    text = "Preferencias",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingSwitchCard(
                    icon = Icons.Default.Notifications,
                    titulo = "Notificaciones",
                    descripcion = "Recibir notificaciones de la app",
                    checked = notificationsEnabled,
                    onCheckedChange = { viewModel.toggleNotifications() }
                )
            }

            item {
                SettingSwitchCard(
                    icon = Icons.Default.DarkMode,
                    titulo = "Modo Oscuro",
                    descripcion = "Activar tema oscuro",
                    checked = darkModeEnabled,
                    onCheckedChange = { viewModel.toggleDarkMode() }
                )
            }

            // Información
            item {
                Text(
                    text = "Información",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingOptionCard(
                    icon = Icons.Default.Info,
                    titulo = "Acerca de",
                    onClick = { /* TODO */ }
                )
            }

            item {
                SettingOptionCard(
                    icon = Icons.Default.PrivacyTip,
                    titulo = "Política de Privacidad",
                    onClick = { /* TODO */ }
                )
            }

            item {
                SettingOptionCard(
                    icon = Icons.Default.Description,
                    titulo = "Términos y Condiciones",
                    onClick = { /* TODO */ }
                )
            }

            item {
                SettingOptionCard(
                    icon = Icons.Default.Help,
                    titulo = "Ayuda y Soporte",
                    onClick = { /* TODO */ }
                )
            }

            // Versión
            item {
                Text(
                    text = "Versión 1.0.0",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Cerrar sesión
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRejected
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Logout, "Cerrar sesión")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            }
        }
    }

    // Diálogo de confirmación
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = "Cerrar sesión",
                    tint = StatusRejected
                )
            },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRejected
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SettingSwitchCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    descripcion: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = titulo,
                tint = EcoGreenPrimary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EcoGreenPrimary,
                    checkedTrackColor = EcoGreenPrimary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun SettingOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = titulo,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}