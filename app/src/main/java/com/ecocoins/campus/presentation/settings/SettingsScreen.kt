package com.ecocoins.campus.presentation.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.presentation.auth.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val BackgroundLight = Color(0xFFF5F5F5)
private val ErrorRed = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECCIÓN: CUENTA
            item {
                SectionHeader(title = "Cuenta")
            }

            item {
                SettingsCard {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Person,
                            title = "Editar Perfil",
                            subtitle = "Actualiza tu información personal",
                            onClick = onNavigateToEditProfile,
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.Lock,
                            title = "Cambiar Contraseña",
                            subtitle = "Actualiza tu contraseña de acceso",
                            onClick = onNavigateToChangePassword,
                            showDivider = false
                        )
                    }
                }
            }

            // SECCIÓN: NOTIFICACIONES
            item {
                SectionHeader(title = "Notificaciones")
            }

            item {
                SettingsCard {
                    Column {
                        SwitchSettingsItem(
                            icon = Icons.Default.Notifications,
                            title = "Notificaciones Push",
                            subtitle = "Recibe alertas sobre tus canjes",
                            checked = uiState.notificacionesActivas,
                            onCheckedChange = { viewModel.toggleNotificaciones(it) },
                            showDivider = true
                        )
                        SwitchSettingsItem(
                            icon = Icons.Default.Email,
                            title = "Notificaciones por Email",
                            subtitle = "Recibe actualizaciones en tu correo",
                            checked = uiState.emailNotificacionesActivas,
                            onCheckedChange = { viewModel.toggleEmailNotificaciones(it) },
                            showDivider = true
                        )
                        SwitchSettingsItem(
                            icon = Icons.Default.Campaign,
                            title = "Recordatorios de Reciclaje",
                            subtitle = "Recibe recordatorios semanales",
                            checked = uiState.recordatoriosActivos,
                            onCheckedChange = { viewModel.toggleRecordatorios(it) },
                            showDivider = false
                        )
                    }
                }
            }

            // SECCIÓN: APARIENCIA
            item {
                SectionHeader(title = "Apariencia")
            }

            item {
                SettingsCard {
                    Column {
                        SwitchSettingsItem(
                            icon = Icons.Default.DarkMode,
                            title = "Modo Oscuro",
                            subtitle = "Cambiar al tema oscuro",
                            checked = uiState.modoOscuroActivo,
                            onCheckedChange = { viewModel.toggleModoOscuro(it) },
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.Language,
                            title = "Idioma",
                            subtitle = "Español",
                            onClick = { /* TODO */ },
                            showDivider = false,
                            trailingContent = {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF9E9E9E)
                                )
                            }
                        )
                    }
                }
            }

            // SECCIÓN: PRIVACIDAD Y SEGURIDAD
            item {
                SectionHeader(title = "Privacidad y Seguridad")
            }

            item {
                SettingsCard {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Shield,
                            title = "Privacidad",
                            subtitle = "Controla quién ve tu información",
                            onClick = onNavigateToPrivacy,
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Seguridad",
                            subtitle = "Configuración de seguridad",
                            onClick = { /* TODO */ },
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.Block,
                            title = "Usuarios Bloqueados",
                            subtitle = "Gestiona usuarios bloqueados",
                            onClick = { /* TODO */ },
                            showDivider = false
                        )
                    }
                }
            }

            // SECCIÓN: SOPORTE
            item {
                SectionHeader(title = "Soporte y Ayuda")
            }

            item {
                SettingsCard {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Help,
                            title = "Centro de Ayuda",
                            subtitle = "Preguntas frecuentes y guías",
                            onClick = { /* TODO */ },
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.Chat,
                            title = "Contactar Soporte",
                            subtitle = "Habla con nuestro equipo",
                            onClick = { /* TODO */ },
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.BugReport,
                            title = "Reportar un Problema",
                            subtitle = "Ayúdanos a mejorar la app",
                            onClick = { /* TODO */ },
                            showDivider = false
                        )
                    }
                }
            }

            // SECCIÓN: INFORMACIÓN
            item {
                SectionHeader(title = "Información")
            }

            item {
                SettingsCard {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Info,
                            title = "Acerca de EcoCoins Campus",
                            subtitle = "Versión 1.0.0",
                            onClick = onNavigateToAbout,
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.Description,
                            title = "Términos y Condiciones",
                            subtitle = "Lee nuestros términos de uso",
                            onClick = { /* TODO */ },
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.Policy,
                            title = "Política de Privacidad",
                            subtitle = "Cómo protegemos tus datos",
                            onClick = { /* TODO */ },
                            showDivider = false
                        )
                    }
                }
            }

            // SECCIÓN: ACCIONES DE CUENTA
            item {
                SectionHeader(title = "Acciones de Cuenta")
            }

            item {
                SettingsCard {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Logout,
                            title = "Cerrar Sesión",
                            subtitle = "Salir de tu cuenta",
                            onClick = { showLogoutDialog = true },
                            titleColor = EcoGreenPrimary,
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.DeleteForever,
                            title = "Eliminar Cuenta",
                            subtitle = "Eliminar permanentemente tu cuenta",
                            onClick = { showDeleteAccountDialog = true },
                            titleColor = ErrorRed,
                            showDivider = false
                        )
                    }
                }
            }

            // Espaciado al final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Diálogo de cerrar sesión
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

    // Diálogo de eliminar cuenta
    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onConfirm = {
                showDeleteAccountDialog = false
                // TODO: Implementar eliminación de cuenta
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF757575),
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        content()
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showDivider: Boolean,
    titleColor: Color = Color(0xFF212121),
    trailingContent: @Composable (() -> Unit)? = null
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EcoGreenLight.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Título y subtítulo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
            }

            // Trailing content
            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E)
                )
            }
        }

        if (showDivider) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFFE0E0E0)
            )
        }
    }
}

@Composable
fun SwitchSettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EcoGreenLight.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Título y subtítulo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
            }

            // Switch
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EcoGreenPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )
        }

        if (showDivider) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFFE0E0E0)
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
                tint = EcoGreenPrimary,
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
                text = "¿Estás seguro que deseas cerrar sesión? Deberás iniciar sesión nuevamente para usar la app.",
                color = Color(0xFF757575)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                )
            ) {
                Text("Cerrar Sesión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF757575))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "⚠️ Eliminar Cuenta",
                fontWeight = FontWeight.Bold,
                color = ErrorRed
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Esta acción es PERMANENTE e IRREVERSIBLE. Se eliminarán:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• Todos tus reciclajes", fontSize = 14.sp, color = Color(0xFF757575))
                    Text("• Todos tus EcoCoins", fontSize = 14.sp, color = Color(0xFF757575))
                    Text("• Tu historial de canjes", fontSize = 14.sp, color = Color(0xFF757575))
                    Text("• Toda tu información personal", fontSize = 14.sp, color = Color(0xFF757575))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Escribe 'ELIMINAR' para confirmar:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                OutlinedTextField(
                    value = confirmText,
                    onValueChange = { confirmText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ELIMINAR") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ErrorRed,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = confirmText == "ELIMINAR",
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed,
                    disabledContainerColor = Color(0xFFE0E0E0)
                )
            ) {
                Text("Eliminar Definitivamente")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF757575))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}