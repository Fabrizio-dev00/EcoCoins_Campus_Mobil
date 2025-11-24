package com.ecocoins.campus.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.Reciclaje
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToReciclajes: () -> Unit,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Animación de escala para el refresh
    var isRefreshing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isRefreshing) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "refresh"
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
                            Icons.Default.Eco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "EcoCoinsCampus",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isRefreshing = true
                        viewModel.loadDashboardData()
                        kotlinx.coroutines.GlobalScope.launch {
                            kotlinx.coroutines.delay(500)
                            isRefreshing = false
                        }
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            modifier = Modifier.scale(if (isRefreshing) 1.2f else 1f)
                        )
                    }
                    IconButton(onClick = onNavigateToPerfil) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        "Cargando...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .scale(scale),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta de EcoCoins con animación
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { -it }
                        ) + fadeIn()
                    ) {
                        EcoCoinsCard(
                            ecoCoins = uiState.user?.ecoCoins?.toDouble() ?: 0.0,
                            userName = uiState.user?.nombre ?: "Usuario",
                            nivel = getNivelNombre(uiState.user?.nivel ?: 1)
                        )
                    }
                }

                // Título de acciones rápidas
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it }
                        ) + fadeIn()
                    ) {
                        Text(
                            "Acciones rápidas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Botones de acciones
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var delay = 0
                        listOf(
                            Triple("Reciclar", Icons.Default.Recycling, onNavigateToReciclajes),
                            Triple("Canjear", Icons.Default.Redeem, onNavigateToRecompensas)
                        ).forEach { (title, icon, onClick) ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(300, delay)
                                ) + fadeIn(animationSpec = tween(300, delay))
                            ) {
                                QuickActionCard(
                                    title = title,
                                    icon = icon,
                                    onClick = onClick,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            delay += 100
                        }
                    }
                }

                // Estadísticas del usuario
                uiState.user?.let { user ->
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally(
                                initialOffsetX = { it }
                            ) + fadeIn()
                        ) {
                            Text(
                                "Mis Estadísticas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = scaleIn() + fadeIn()
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatRow(
                                        "Total reciclajes",
                                        "${user.totalReciclajes}",
                                        Icons.Default.Recycling
                                    )
                                    StatRow(
                                        "Kg reciclados",
                                        String.format("%.2f kg", user.totalKgReciclados),
                                        Icons.Default.Scale
                                    )
                                    StatRow(
                                        "Nivel",
                                        getNivelNombre(user.nivel),
                                        Icons.Default.EmojiEvents
                                    )
                                }
                            }
                        }
                    }
                }

                // Últimos reciclajes
                if (uiState.reciclajes.isNotEmpty()) {
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally(
                                initialOffsetX = { -it }
                            ) + fadeIn()
                        ) {
                            Text(
                                "Últimos Reciclajes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    items(uiState.reciclajes.take(5)) { reciclaje ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally(
                                initialOffsetX = { it }
                            ) + fadeIn()
                        ) {
                            ReciclajeCard(reciclaje)
                        }
                    }
                }
            }
        }

        // Mensaje de error
        uiState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it }
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { it }
                    ) + fadeOut()
                ) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("OK")
                            }
                        }
                    ) {
                        Text(error)
                    }
                }
            }
        }
    }
}

@Composable
fun EcoCoinsCard(ecoCoins: Double, userName: String, nivel: String) {
    // Animación de brillo
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary
                        ),
                        startX = shimmer * 1000
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.WavingHand,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "Hola, $userName",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Nivel $nivel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Paid,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Column {
                        Text(
                            "Tu saldo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Text(
                            String.format("%.0f EcoCoins", ecoCoins),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press"
    )

    Card(
        onClick = {
            isPressed = true
            kotlinx.coroutines.GlobalScope.launch {
                kotlinx.coroutines.delay(100)
                isPressed = false
                onClick()
            }
        },
        modifier = modifier.scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun StatRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
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
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun ReciclajeCard(reciclaje: Reciclaje) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Recycling,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        reciclaje.tipoMaterial,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        String.format("%.2f kg", reciclaje.pesoKg),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        reciclaje.fecha.take(10),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "+${reciclaje.ecoCoinsGanadas}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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