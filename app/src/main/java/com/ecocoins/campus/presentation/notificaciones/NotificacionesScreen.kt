package com.ecocoins.campus.presentation.notificaciones

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.Notificacion
import com.ecocoins.campus.data.model.TipoNotificacion
import java.text.SimpleDateFormat
import java.util.*

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificacionesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotificaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Notificaciones",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        if (uiState.noLeidas > 0) {
                            Text(
                                text = "${uiState.noLeidas} sin leer",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (uiState.noLeidas > 0) {
                        TextButton(onClick = { viewModel.marcarTodasLeidas() }) {
                            Text(
                                "Marcar todas",
                                color = EcoGreenPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EcoGreenPrimary)
            }
        } else if (uiState.notificaciones.isEmpty()) {
            EmptyNotificacionesState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.notificaciones,
                    key = { it.id }
                ) { notificacion ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                    ) {
                        NotificacionCard(
                            notificacion = notificacion,
                            onMarcarLeida = { viewModel.marcarLeida(it) },
                            onClick = { /* TODO: Navegar según tipo */ }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun NotificacionCard(
    notificacion: Notificacion,
    onMarcarLeida: (String) -> Unit,
    onClick: (Notificacion) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!notificacion.leida) {
                    onMarcarLeida(notificacion.id)
                }
                onClick(notificacion)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) Color.White else EcoGreenLight.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getNotificacionColor(notificacion.tipo).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificacionIcon(notificacion.tipo),
                    contentDescription = null,
                    tint = getNotificacionColor(notificacion.tipo),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Contenido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notificacion.titulo,
                        fontSize = 16.sp,
                        fontWeight = if (notificacion.leida) FontWeight.Medium else FontWeight.Bold,
                        color = Color(0xFF212121),
                        modifier = Modifier.weight(1f)
                    )

                    if (!notificacion.leida) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EcoGreenPrimary)
                        )
                    }
                }

                Text(
                    text = notificacion.mensaje,
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    lineHeight = 20.sp
                )

                Text(
                    text = formatTiempoRelativo(notificacion.fecha),
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
fun EmptyNotificacionesState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFE0E0E0)
            )

            Text(
                text = "No tienes notificaciones",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Text(
                text = "Te avisaremos cuando tengas algo nuevo",
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

fun getNotificacionIcon(tipo: TipoNotificacion): ImageVector {
    return when (tipo) {
        TipoNotificacion.CANJE_LISTO -> Icons.Default.CardGiftcard
        TipoNotificacion.NUEVA_RECOMPENSA -> Icons.Default.NewReleases
        TipoNotificacion.LOGRO_DESBLOQUEADO -> Icons.Default.EmojiEvents
        TipoNotificacion.RECORDATORIO -> Icons.Default.Notifications
        TipoNotificacion.SISTEMA -> Icons.Default.Info
        TipoNotificacion.SOCIAL -> Icons.Default.People
    }
}

fun getNotificacionColor(tipo: TipoNotificacion): Color {
    return when (tipo) {
        TipoNotificacion.CANJE_LISTO -> Color(0xFF4CAF50)
        TipoNotificacion.NUEVA_RECOMPENSA -> Color(0xFFFF9800)
        TipoNotificacion.LOGRO_DESBLOQUEADO -> Color(0xFFFFD700)
        TipoNotificacion.RECORDATORIO -> Color(0xFF2196F3)
        TipoNotificacion.SISTEMA -> Color(0xFF9E9E9E)
        TipoNotificacion.SOCIAL -> Color(0xFF9C27B0)
    }
}

fun formatTiempoRelativo(fecha: String): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = format.parse(fecha)
        val now = Date()

        val diff = now.time - (date?.time ?: 0)
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            days > 0 -> "Hace ${days}d"
            hours > 0 -> "Hace ${hours}h"
            minutes > 0 -> "Hace ${minutes}m"
            else -> "Ahora"
        }
    } catch (e: Exception) {
        "Reciente"
    }
}