package com.ecocoins.campus.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToReciclajes: () -> Unit,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToLogros: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val user by viewModel.user.observeAsState()
    val resumenEstadisticas by viewModel.resumenEstadisticas.observeAsState()
    val ecoCoins by viewModel.ecoCoins.observeAsState(0L)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hola, ${user?.nombre?.split(" ")?.firstOrNull() ?: "Usuario"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "¡Bienvenido de vuelta!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, "Notificaciones")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando dashboard...")
            }
            error != null -> {
                ErrorState(
                    message = error ?: "Error desconocido",
                    onRetry = { viewModel.refreshData() }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // EcoCoins Balance Card
                    item {
                        EcoCoinsBalanceCard(
                            ecoCoins = ecoCoins,
                            nivel = user?.nivel ?: 1
                        )
                    }

                    // Quick Actions
                    item {
                        Text(
                            text = "Acciones Rápidas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        QuickActionsRow(
                            onNavigateToReciclajes = onNavigateToReciclajes,
                            onNavigateToRecompensas = onNavigateToRecompensas,
                            onNavigateToEstadisticas = onNavigateToEstadisticas,
                            onNavigateToLogros = onNavigateToLogros
                        )
                    }

                    // Resumen de Estadísticas
                    item {
                        when (resumenEstadisticas) {
                            is Resource.Success -> {
                                val resumen = (resumenEstadisticas as Resource.Success).data
                                if (resumen != null) {
                                    Text(
                                        text = "Tu Impacto",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    ImpactoStatsCard(
                                        // ✅ Acceder a los valores del Map con conversión segura
                                        totalReciclajes = (resumen["totalReciclajes"] as? Number)?.toInt() ?: 0,
                                        kgReciclados = (resumen["totalKgReciclados"] as? Number)?.toDouble() ?: 0.0,
                                        ecoCoinsGanados = (resumen["ecoCoinsGanados"] as? Number)?.toLong() ?: 0L
                                    )
                                }
                            }
                            else -> {}
                        }
                    }

                    // Consejos Ecológicos
                    item {
                        Text(
                            text = "Consejos del Día",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        EcoTipCard()
                    }
                }
            }
        }
    }
}

@Composable
private fun EcoCoinsBalanceCard(
    ecoCoins: Long,
    nivel: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Saldo Disponible",
                            color = BackgroundWhite.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$ecoCoins",
                                color = BackgroundWhite,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EC",
                                color = BackgroundWhite.copy(alpha = 0.8f),
                                fontSize = 18.sp
                            )
                        }
                    }

                    // Nivel Badge
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(BackgroundWhite.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Nivel",
                                color = BackgroundWhite,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "$nivel",
                                color = BackgroundWhite,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "¡Sigue reciclando para ganar más EcoCoins!",
                    color = BackgroundWhite.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onNavigateToReciclajes: () -> Unit,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToLogros: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            icon = Icons.Default.Recycling,
            label = "Reciclar",
            containerColor = EcoGreenSecondary,
            modifier = Modifier.weight(1f),
            onClick = onNavigateToReciclajes
        )
        QuickActionButton(
            icon = Icons.Default.CardGiftcard,
            label = "Canjear",
            containerColor = EcoOrange,
            modifier = Modifier.weight(1f),
            onClick = onNavigateToRecompensas
        )
        QuickActionButton(
            icon = Icons.Default.BarChart,
            label = "Stats",
            containerColor = PlasticBlue,
            modifier = Modifier.weight(1f),
            onClick = onNavigateToEstadisticas
        )
        QuickActionButton(
            icon = Icons.Default.EmojiEvents,
            label = "Logros",
            containerColor = EcoOrangeLight,
            modifier = Modifier.weight(1f),
            onClick = onNavigateToLogros
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    containerColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = BackgroundWhite,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = BackgroundWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ImpactoStatsCard(
    totalReciclajes: Int,
    kgReciclados: Double,
    ecoCoinsGanados: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ImpactoStatItem(
                    value = "$totalReciclajes",
                    label = "Reciclajes",
                    icon = Icons.Default.Recycling
                )
                ImpactoStatItem(
                    value = String.format("%.1f kg", kgReciclados),
                    label = "Reciclados",
                    icon = Icons.Default.Scale
                )
                ImpactoStatItem(
                    value = "$ecoCoinsGanados",
                    label = "EC Ganados",
                    icon = Icons.Default.AttachMoney
                )
            }
        }
    }
}

@Composable
private fun ImpactoStatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = EcoGreenPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EcoTipCard() {
    val tips = listOf(
        "💡 Separa tus residuos antes de reciclar para ganar más EcoCoins",
        "🌿 El vidrio puede reciclarse infinitas veces sin perder calidad",
        "♻️ Una botella de plástico tarda 450 años en degradarse",
        "🌍 Reciclar 1 tonelada de papel salva 17 árboles",
        "💧 Reciclar aluminio ahorra 95% de la energía necesaria para producirlo"
    )

    val randomTip = remember { tips.random() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenLight.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Consejo",
                tint = EcoGreenPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = randomTip,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}