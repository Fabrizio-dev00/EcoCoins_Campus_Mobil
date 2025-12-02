package com.ecocoins.campus.presentation.history

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
import com.ecocoins.campus.data.model.Canje
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanjesHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: CanjesHistoryViewModel = hiltViewModel()
) {
    val historial by viewModel.historial.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Canjes") },
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
                    icon = Icons.Default.CardGiftcard,
                    title = "Sin canjes",
                    message = "Aún no has canjeado ninguna recompensa"
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
                        CanjesResumenCard(historial)
                    }

                    item {
                        Text(
                            text = "Todos los Canjes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(historial) { canje ->
                        CanjeHistoryCard(canje)
                    }
                }
            }
        }
    }
}

@Composable
private fun CanjesResumenCard(historial: List<Canje>) {
    val totalGastado = historial.sumOf { it.costoEcoCoins }
    val completados = historial.count { it.estado == "COMPLETADO" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoOrange
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
                    text = "Total Gastado",
                    color = BackgroundWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = "$totalGastado EC",
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
                    text = "Canjes totales",
                    color = BackgroundWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$completados completados",
                    color = BackgroundWhite.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun CanjeHistoryCard(canje: Canje) {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = canje.recompensaNombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = canje.fecha.toFormattedDate(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                CanjeEstadoBadge(canje.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Costo",
                        tint = EcoOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${canje.costoEcoCoins} EC",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                }

                if (canje.codigoCanje != null && canje.estado == "COMPLETADO") {
                    TextButton(onClick = { /* Mostrar código */ }) {
                        Icon(Icons.Default.QrCode, "Código", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver código")
                    }
                }
            }
        }
    }
}

@Composable
private fun CanjeEstadoBadge(estado: String) {
    val (color, text) = when (estado) {
        "COMPLETADO" -> StatusCompleted to "Completado"
        "PENDIENTE" -> StatusPending to "Pendiente"
        "CANCELADO" -> StatusRejected to "Cancelado"
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