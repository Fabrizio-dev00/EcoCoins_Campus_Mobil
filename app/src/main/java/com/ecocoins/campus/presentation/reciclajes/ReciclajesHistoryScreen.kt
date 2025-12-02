package com.ecocoins.campus.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.presentation.reciclajes.ReciclajesHistoryViewModel
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciclajesHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReciclajesHistoryViewModel = hiltViewModel()
) {
    val historial by viewModel.historial.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Reciclajes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshHistorial() }) {
                        Icon(Icons.Default.Refresh, "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando historial...")
            }
            error != null -> {
                ErrorState(
                    message = error ?: "Error desconocido",
                    onRetry = { viewModel.refreshHistorial() }
                )
            }
            historial.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.History,
                    title = "Sin historial",
                    message = "Aún no tienes reciclajes en tu historial"
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Resumen
                    item {
                        HistorialResumenCard(historial)
                    }

                    // Filtros por mes (opcional)
                    item {
                        Text(
                            text = "Todos los Reciclajes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Lista de reciclajes
                    items(historial) { reciclaje ->
                        ReciclajeHistoryCard(reciclaje)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorialResumenCard(historial: List<Reciclaje>) {
    val totalEcoCoins = historial.sumOf { it.ecoCoinsGanados }
    val totalKg = historial.sumOf { it.peso }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Ganado",
                    color = BackgroundWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = "+$totalEcoCoins EC",
                    color = BackgroundWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${historial.size}",
                    color = BackgroundWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Reciclajes",
                    color = BackgroundWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%.1f", totalKg)} kg",
                    color = BackgroundWhite.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ReciclajeHistoryCard(reciclaje: Reciclaje) {
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
            // Icono del material
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = getMaterialColor(reciclaje.materialTipo).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getMaterialIcon(reciclaje.materialTipo),
                    contentDescription = reciclaje.materialTipo,
                    tint = getMaterialColor(reciclaje.materialTipo),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reciclaje.materialTipo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${reciclaje.peso} kg • ${reciclaje.cantidad} items",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = reciclaje.fecha.toFormattedDate(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // EcoCoins y Estado
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${reciclaje.ecoCoinsGanados} EC",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoOrange
                )
                Spacer(modifier = Modifier.height(4.dp))
                EstadoBadge(reciclaje.estado)
            }
        }
    }
}

@Composable
private fun EstadoBadge(estado: String) {
    val (color, text) = when (estado) {
        "VALIDADO" -> StatusCompleted to "Validado"
        "PENDIENTE" -> StatusPending to "Pendiente"
        "RECHAZADO" -> StatusRejected to "Rechazado"
        else -> MaterialTheme.colorScheme.onSurfaceVariant to estado
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun getMaterialIcon(material: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (material.uppercase()) {
        "PLASTICO" -> Icons.Default.Recycling
        "PAPEL" -> Icons.Default.Description
        "VIDRIO" -> Icons.Default.LocalDrink
        "METAL" -> Icons.Default.Build
        "ELECTRONICO" -> Icons.Default.PhoneAndroid
        "ORGANICO" -> Icons.Default.Grass
        else -> Icons.Default.Recycling
    }
}

private fun getMaterialColor(material: String): androidx.compose.ui.graphics.Color {
    return when (material.uppercase()) {
        "PLASTICO" -> PlasticBlue
        "PAPEL" -> PaperBrown
        "VIDRIO" -> GlassGreen
        "METAL" -> MetalGray
        "ELECTRONICO" -> PlasticBlue
        "ORGANICO" -> GlassGreen
        else -> EcoGreenSecondary
    }
}