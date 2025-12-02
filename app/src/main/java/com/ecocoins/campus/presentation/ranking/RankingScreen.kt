package com.ecocoins.campus.presentation.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.ecocoins.campus.data.model.RankingItem
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onNavigateBack: () -> Unit,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val ranking by viewModel.ranking.observeAsState(emptyList())
    val posicionUsuario by viewModel.posicionUsuario.observeAsState()
    val tipoRanking by viewModel.tipoRanking.observeAsState("global")
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Global", "Semanal", "Mensual")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshRanking() }) {
                        Icon(Icons.Default.Refresh, "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            val tipo = when (index) {
                                0 -> "global"
                                1 -> "semanal"
                                2 -> "mensual"
                                else -> "global"
                            }
                            viewModel.loadRanking(tipo)
                        },
                        text = { Text(title) }
                    )
                }
            }

            when {
                isLoading -> {
                    LoadingState(message = "Cargando ranking...")
                }
                error != null -> {
                    ErrorState(
                        message = error ?: "Error desconocido",
                        onRetry = { viewModel.refreshRanking() }
                    )
                }
                ranking.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.EmojiEvents,
                        title = "Sin datos de ranking",
                        message = "No hay información disponible"
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Posición del usuario
                        item {
                            posicionUsuario?.let { posicion ->
                                PosicionUsuarioCard(posicion)
                            }
                        }

                        // Top 3
                        item {
                            Text(
                                text = "Top 3",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val top3 = ranking.take(3)
                        itemsIndexed(top3) { _, item ->
                            Top3Card(item)
                        }

                        // Resto del ranking
                        if (ranking.size > 3) {
                            item {
                                Text(
                                    text = "Demás Posiciones",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            itemsIndexed(ranking.drop(3)) { _, item ->
                                RankingCard(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PosicionUsuarioCard(posicion: com.ecocoins.campus.data.model.PosicionUsuario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Tu Posición",
                color = BackgroundWhite.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "#${posicion.posicion}",
                        color = BackgroundWhite,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "de ${posicion.totalUsuarios}",
                        color = BackgroundWhite.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${posicion.ecoCoinsGanados} EC",
                        color = BackgroundWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${String.format("%.1f", posicion.kgReciclados)} kg",
                        color = BackgroundWhite.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun Top3Card(item: RankingItem) {
    val medalColor = when (item.posicion) {
        1 -> EcoOrange
        2 -> MetalGray
        3 -> PaperBrown
        else -> EcoGreenSecondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = medalColor.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Medalla
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(medalColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (item.posicion) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "${item.posicion}"
                    },
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del usuario
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nivel ${item.nivel} • ${item.totalReciclajes} reciclajes",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "${item.ecoCoinsGanados} EC",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoOrange
                    )
                    Text(
                        text = " • ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${String.format("%.1f", item.kgReciclados)} kg",
                        fontSize = 14.sp,
                        color = EcoGreenPrimary
                    )
                }
            }

            // Porcentaje
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.1f%%", item.porcentaje),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = medalColor
                )
            }
        }
    }
}

@Composable
private fun RankingCard(item: RankingItem) {
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
            // Posición
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${item.posicion}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${item.ecoCoinsGanados} EC • ${String.format("%.1f", item.kgReciclados)} kg",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Nivel
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EcoGreenPrimary.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "Nv. ${item.nivel}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}