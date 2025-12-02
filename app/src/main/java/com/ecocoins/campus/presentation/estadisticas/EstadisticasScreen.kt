package com.ecocoins.campus.presentation.estadisticas

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.MaterialStats
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    onNavigateBack: () -> Unit,
    viewModel: EstadisticasViewModel = hiltViewModel()
) {
    val estadisticas by viewModel.estadisticas.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshEstadisticas() }) {
                        Icon(Icons.Default.Refresh, "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando estadísticas...")
            }
            error != null -> {
                ErrorState(
                    message = error ?: "Error desconocido",
                    onRetry = { viewModel.refreshEstadisticas() }
                )
            }
            estadisticas == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay estadísticas disponibles")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen General
                    item {
                        Text(
                            text = "Resumen General",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ResumenGeneralCard(estadisticas!!)
                    }

                    // Distribución de Materiales
                    item {
                        Text(
                            text = "Materiales Reciclados",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(estadisticas!!.distribucionMateriales) { material ->
                        MaterialStatsCard(material)
                    }

                    // Impacto Ambiental
                    item {
                        Text(
                            text = "Tu Impacto Ambiental",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ImpactoAmbientalCard(estadisticas!!)
                    }

                    // Rachas
                    item {
                        RachasCard(estadisticas!!)
                    }

                    // Comparativas
                    item {
                        ComparativasCard(estadisticas!!)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenGeneralCard(estadisticas: com.ecocoins.campus.data.model.EstadisticasDetalladas) {
    val resumen = estadisticas.resumenGeneral

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nivel ${resumen.nivel}",
                        color = BackgroundWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${resumen.ecoCoinsActuales} EcoCoins",
                        color = BackgroundWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(BackgroundWhite.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = BackgroundWhite.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    value = "${resumen.totalReciclajes}",
                    label = "Reciclajes",
                    color = BackgroundWhite
                )
                StatItem(
                    value = String.format("%.1f kg", resumen.totalKgReciclados),
                    label = "Reciclados",
                    color = BackgroundWhite
                )
                StatItem(
                    value = "+${resumen.ecoCoinsGanados}",
                    label = "EC Ganados",
                    color = BackgroundWhite
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = color.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MaterialStatsCard(material: MaterialStats) {
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
                        color = getMaterialColor(material.material).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getMaterialIcon(material.material),
                    contentDescription = material.material,
                    tint = getMaterialColor(material.material),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.material,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${material.cantidad} reciclajes • ${String.format("%.1f kg", material.kgTotales)}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Porcentaje y EcoCoins
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.0f%%", material.porcentaje),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getMaterialColor(material.material)
                )
                Text(
                    text = "+${material.ecoCoins} EC",
                    fontSize = 12.sp,
                    color = EcoOrange
                )
            }
        }
    }
}

@Composable
private fun ImpactoAmbientalCard(estadisticas: com.ecocoins.campus.data.model.EstadisticasDetalladas) {
    val impacto = estadisticas.impactoAmbiental

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenLight.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Impacto",
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Has evitado ${String.format("%.1f", impacto.co2Evitado)} kg de CO₂",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Equivalente a:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            EquivalenciaItem(
                icon = "🌳",
                text = "${impacto.equivalencias.arboles} árboles plantados"
            )
            Spacer(modifier = Modifier.height(8.dp))
            EquivalenciaItem(
                icon = "⚡",
                text = "${String.format("%.1f", impacto.equivalencias.energia)} kWh de energía ahorrada"
            )
            Spacer(modifier = Modifier.height(8.dp))
            EquivalenciaItem(
                icon = "💧",
                text = "${String.format("%.1f", impacto.equivalencias.agua)} litros de agua ahorrada"
            )
        }
    }
}

@Composable
private fun EquivalenciaItem(icon: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RachasCard(estadisticas: com.ecocoins.campus.data.model.EstadisticasDetalladas) {
    val rachas = estadisticas.rachas

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoOrangeLight.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RachaItem(
                icon = Icons.Default.LocalFireDepartment,
                value = "${rachas.rachaActual}",
                label = "Racha Actual",
                color = EcoOrange
            )
            RachaItem(
                icon = Icons.Default.EmojiEvents,
                value = "${rachas.mejorRacha}",
                label = "Mejor Racha",
                color = EcoGreenPrimary
            )
        }
    }
}

@Composable
private fun RachaItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ComparativasCard(estadisticas: com.ecocoins.campus.data.model.EstadisticasDetalladas) {
    val comparativas = estadisticas.comparativas

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PlasticBlue.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Comparativa con otros usuarios",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            ComparativaItem(
                label = "Tu posición",
                value = "#${comparativas.tuPosicion}"
            )
            Spacer(modifier = Modifier.height(8.dp))
            ComparativaItem(
                label = "Promedio general",
                value = "${String.format("%.1f", comparativas.promedioGeneral)} kg"
            )
            Spacer(modifier = Modifier.height(8.dp))
            ComparativaItem(
                label = "Superas al",
                value = "${String.format("%.0f", comparativas.porcentajeSuperior)}% de usuarios"
            )
        }
    }
}

@Composable
private fun ComparativaItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = PlasticBlue
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