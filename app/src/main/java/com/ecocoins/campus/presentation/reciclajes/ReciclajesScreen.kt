package com.ecocoins.campus.presentation.reciclajes

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
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.toShortDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciclajesScreen(
    onNavigateToScanner: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ReciclajesViewModel = hiltViewModel()
) {
    val reciclajes by viewModel.reciclajes.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reciclajes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, "Historial")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToScanner,
                icon = { Icon(Icons.Default.QrCodeScanner, "Escanear") },
                text = { Text("Reciclar Ahora") },
                containerColor = EcoGreenPrimary
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando reciclajes...")
            }
            error != null -> {
                ErrorState(
                    message = error ?: "Error desconocido",
                    onRetry = { viewModel.loadReciclajes() }
                )
            }
            reciclajes.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.Recycling,
                    title = "Sin reciclajes",
                    message = "Aún no has registrado ningún reciclaje.\n¡Comienza ahora y gana EcoCoins!"
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
                        ResumenReciclajesCard(reciclajes)
                    }

                    // Lista de reciclajes
                    item {
                        Text(
                            text = "Reciclajes Recientes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(reciclajes.take(5)) { reciclaje ->
                        ReciclajeCard(reciclaje)
                    }

                    if (reciclajes.size > 5) {
                        item {
                            TextButton(
                                onClick = onNavigateToHistory,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver todos los reciclajes")
                                Icon(Icons.Default.ArrowForward, "Ver más")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenReciclajesCard(reciclajes: List<Reciclaje>) {
    val totalKg = reciclajes.sumOf { it.peso }
    val totalEcoCoins = reciclajes.sumOf { it.ecoCoinsGanados }
    val validados = reciclajes.count { it.estado == "VALIDADO" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenLight.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ResumenItem(
                value = "${reciclajes.size}",
                label = "Total",
                icon = Icons.Default.Recycling
            )
            ResumenItem(
                value = String.format("%.1f kg", totalKg),
                label = "Reciclados",
                icon = Icons.Default.Scale
            )
            ResumenItem(
                value = "$validados",
                label = "Validados",
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

@Composable
private fun ResumenItem(
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
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ReciclajeCard(reciclaje: Reciclaje) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = "${reciclaje.peso} kg • ${reciclaje.fecha.toShortDate()}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Estado y EcoCoins
            Column(horizontalAlignment = Alignment.End) {
                EstadoBadge(reciclaje.estado)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "+${reciclaje.ecoCoinsGanados} EC",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoOrange
                )
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