package com.ecocoins.campus.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.Reciclaje
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Colores personalizados de EcoCoins Campus
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val EcoOrangeLight = Color(0xFFFFB74D)
private val StatusCompleted = Color(0xFF4CAF50)
private val StatusPending = Color(0xFFFFB74D)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToReciclajes: () -> Unit,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToLogros: () -> Unit,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToNotificaciones: () -> Unit,
    onNavigateToReferidos: () -> Unit,
    onNavigateToMapa: () -> Unit,
    onNavigateToEducacion: () -> Unit,
    onNavigateToSoporte: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    // ✅ CAMBIO: Usar observeAsState() para LiveData
    val currentUser by viewModel.currentUser.observeAsState()
    val notificacionesNoLeidas by viewModel.notificacionesNoLeidas.observeAsState(0)

    val scope = rememberCoroutineScope()

    // Animación de escala para el refresh
    var isRefreshing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isRefreshing) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "refresh"
    )

    Scaffold(
        topBar = {
            DashboardTopBar(
                ecoCoins = currentUser?.ecoCoins ?: 0,
                onRefreshClick = {
                    isRefreshing = true
                    viewModel.refresh()
                    scope.launch {
                        delay(500)
                        isRefreshing = false
                    }
                },
                onNotificationsClick = onNavigateToNotificaciones,
                isRefreshing = isRefreshing
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .scale(scale),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Saludo personalizado
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                ) {
                    GreetingSection(userName = currentUser?.nombre ?: "Usuario")
                }
            }

            // Botón principal de reciclaje
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                ) {
                    RecycleNowCard(onClick = onNavigateToReciclajes)
                }
            }

            // Acciones rápidas
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Acciones rápidas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                title = "Canjear",
                                icon = Icons.Default.Redeem,
                                onClick = onNavigateToRecompensas,
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                title = "Mi Perfil",
                                icon = Icons.Default.Person,
                                onClick = onNavigateToPerfil,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ===== SECCIÓN GAMIFICACIÓN - FASE 2 =====
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Gamificación 🎮",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GamificationCard(
                                title = "Ranking",
                                icon = "🏆",
                                description = "Top usuarios",
                                onClick = onNavigateToRanking,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFFFD700)
                            )
                            GamificationCard(
                                title = "Logros",
                                icon = "🎖️",
                                description = "Tus logros",
                                onClick = onNavigateToLogros,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF9C27B0)
                            )
                        }

                        GamificationCard(
                            title = "Estadísticas Detalladas",
                            icon = "📊",
                            description = "Análisis completo de tu impacto",
                            onClick = onNavigateToEstadisticas,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF2196F3),
                            isWide = true
                        )
                    }
                }
            }

            // ===== SECCIÓN COMUNIDAD - FASE 3 =====
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Comunidad 🌍",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GamificationCard(
                                title = "Referidos",
                                icon = "👥",
                                description = "Invita amigos",
                                onClick = onNavigateToReferidos,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF4CAF50)
                            )
                            GamificationCard(
                                title = "Mapa",
                                icon = "🗺️",
                                description = "Puntos cerca",
                                onClick = onNavigateToMapa,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF00BCD4)
                            )
                        }
                    }
                }
            }

            // ===== SECCIÓN APRENDIZAJE Y AYUDA - FASE 4 =====
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Aprendizaje y Ayuda 📚",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GamificationCard(
                                title = "Educación",
                                icon = "📖",
                                description = "Aprende más",
                                onClick = onNavigateToEducacion,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFFF5722)
                            )
                            GamificationCard(
                                title = "Soporte",
                                icon = "🎧",
                                description = "¿Necesitas ayuda?",
                                onClick = onNavigateToSoporte,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF673AB7)
                            )
                        }
                    }
                }
            }

            // Estadísticas del usuario
            currentUser?.let { user ->
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "Mis Estadísticas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )

                            StatsCard(
                                totalReciclajes = user.totalReciclajes,
                                totalKg = user.totalKgReciclados,
                                nivel = user.nivel
                            )
                        }
                    }
                }
            }

            // Mensaje de estado vacío
            item {
                EmptyActivitiesCard()
            }

            // Espaciado final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    ecoCoins: Int,
    onRefreshClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    isRefreshing: Boolean
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = tween(500),
        label = "rotation"
    )

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "EcoCoins Campus",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary
                )
            }
        },
        actions = {
            // EcoCoins badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = EcoOrangeLight.copy(alpha = 0.2f),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = "EcoCoins",
                        tint = EcoOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = ecoCoins.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                }
            }

            // Notifications icon
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun GreetingSection(userName: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "¡Hola, $userName!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(modifier = Modifier.height(4.dp))

        val currentTime = remember {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }

        Text(
            text = "Última actualización: $currentTime",
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
fun RecycleNowCard(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                kotlinx.coroutines.GlobalScope.launch {
                    delay(100)
                    isPressed = false
                    onClick()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¡Recicla Ahora!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gana EcoCoins por cada material reciclado",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Recycling,
                        contentDescription = "Reciclar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
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
                delay(100)
                isPressed = false
                onClick()
            }
        },
        modifier = modifier.scale(scale),
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
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = EcoGreenPrimary
                    )
                }
            }

            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )
        }
    }
}

@Composable
fun GamificationCard(
    title: String,
    icon: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color,
    isWide: Boolean = false
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
                delay(100)
                isPressed = false
                onClick()
            }
        },
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = icon,
                            fontSize = 24.sp
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        description,
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = icon,
                            fontSize = 24.sp
                        )
                    }
                }

                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    description,
                    fontSize = 11.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
fun StatsCard(
    totalReciclajes: Int,
    totalKg: Double,
    nivel: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatRow(
                label = "Total reciclajes",
                value = "$totalReciclajes",
                icon = Icons.Default.Recycling
            )
            HorizontalDivider(color = Color(0xFFE0E0E0))
            StatRow(
                label = "Kg reciclados",
                value = String.format("%.2f kg", totalKg),
                icon = Icons.Default.Scale
            )
            HorizontalDivider(color = Color(0xFFE0E0E0))
            StatRow(
                label = "Nivel",
                value = getNivelNombre(nivel),
                icon = Icons.Default.EmojiEvents
            )
        }
    }
}

@Composable
fun StatRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
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
                tint = EcoGreenPrimary
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF212121)
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = EcoGreenPrimary
        )
    }
}

@Composable
fun EmptyActivitiesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Sin actividades",
                tint = Color(0xFF757575),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sin actividades recientes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¡Comienza a reciclar para ganar EcoCoins!",
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
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