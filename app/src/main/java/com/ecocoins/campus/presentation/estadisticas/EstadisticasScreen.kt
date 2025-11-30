package com.ecocoins.campus.presentation.estadisticas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.EstadisticasDetalladas
import com.ecocoins.campus.data.model.MaterialStats
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.model.TendenciaDia
import kotlin.math.roundToInt

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF5F5F5)

// Colores para materiales
private val PlasticoColor = Color(0xFF2196F3)
private val PapelColor = Color(0xFF8D6E63)
private val VidrioColor = Color(0xFF4CAF50)
private val MetalColor = Color(0xFF9E9E9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    onNavigateBack: () -> Unit,
    viewModel: EstadisticasViewModel = hiltViewModel()
) {
    val estadisticasState by viewModel.estadisticas.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEstadisticas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Estadísticas 📊",
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
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = EcoGreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        when (estadisticasState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EcoGreenPrimary)
                }
            }
            is Resource.Success -> {
                val estadisticas = (estadisticasState as Resource.Success).data!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen General
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                        ) {
                            ResumenGeneralCard(estadisticas = estadisticas)
                        }
                    }

                    // Distribución por Material
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Distribución por Material",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )

                                MaterialesDistribucionCard(
                                    materiales = estadisticas.distribucionMateriales
                                )
                            }
                        }
                    }

                    // Gráfico de Tendencia Semanal
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Tendencia Semanal",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )

                                TendenciaSemanolCard(
                                    tendencias = estadisticas.tendenciaSemanal
                                )
                            }
                        }
                    }

                    // Impacto Ambiental
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Tu Impacto Ambiental 🌍",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )

                                ImpactoAmbientalCard(estadisticas = estadisticas)
                            }
                        }
                    }

                    // Comparativas
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Comparativas",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )

                                ComparativasCard(estadisticas = estadisticas)
                            }
                        }
                    }

                    // Rachas
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Rachas 🔥",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )

                                RachasCard(estadisticas = estadisticas)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            is Resource.Error -> {
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
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = (estadisticasState as Resource.Error).message ?: "Error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                        Button(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ResumenGeneralCard(estadisticas: EstadisticasDetalladas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen General",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenStatItem(
                    icon = Icons.Default.Recycling,
                    value = "${estadisticas.resumenGeneral.totalReciclajes}",
                    label = "Reciclajes",
                    modifier = Modifier.weight(1f)
                )
                ResumenStatItem(
                    icon = Icons.Default.Scale,
                    value = String.format("%.1f", estadisticas.resumenGeneral.totalKgReciclados),
                    label = "Kg Total",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenStatItem(
                    icon = Icons.Default.TrendingUp,
                    value = "${estadisticas.resumenGeneral.ecoCoinsGanados}",
                    label = "Ganados",
                    modifier = Modifier.weight(1f)
                )
                ResumenStatItem(
                    icon = Icons.Default.EmojiEvents,
                    value = estadisticas.resumenGeneral.nivel,
                    label = "Nivel",
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EcoCoins Actuales",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = null,
                            tint = EcoOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "${estadisticas.resumenGeneral.ecoCoinsActuales}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MaterialesDistribucionCard(materiales: List<MaterialStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            materiales.forEach { material ->
                MaterialProgressBar(material = material)
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${materiales.sumOf { it.cantidad }}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                    Text(
                        text = "Total Items",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f kg", materiales.sumOf { it.kgTotales }),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                    Text(
                        text = "Peso Total",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = EcoOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${materiales.sumOf { it.ecoCoins }}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                }
            }
        }
    }
}

@Composable
fun MaterialProgressBar(material: MaterialStats) {
    val color = getMaterialColor(material.material)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = material.material,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
            }

            Text(
                text = "${material.porcentaje.roundToInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        LinearProgressIndicator(
            progress = { (material.porcentaje / 100f).toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${material.cantidad} items",
                fontSize = 11.sp,
                color = Color(0xFF757575)
            )
            Text(
                text = "${String.format("%.1f", material.kgTotales)} kg",
                fontSize = 11.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun TendenciaSemanolCard(tendencias: List<TendenciaDia>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tendencias.isNotEmpty()) {
                val maxCantidad = tendencias.maxOf { it.reciclajes }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    tendencias.forEach { dato ->
                        TendenciaBar(
                            dato = dato,
                            maxCantidad = maxCantidad,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${tendencias.sumOf { it.reciclajes }}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text(
                            text = "Reciclajes",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            } else {
                EmptyTendenciaMessage()
            }
        }
    }
}

@Composable
fun TendenciaBar(
    dato: TendenciaDia,
    maxCantidad: Int,
    modifier: Modifier = Modifier
) {
    val heightFraction = if (maxCantidad > 0) {
        (dato.reciclajes.toFloat() / maxCantidad.toFloat()).coerceIn(0.1f, 1f)
    } else 0.1f

    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (dato.reciclajes > 0) {
            Text(
                text = "${dato.reciclajes}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(heightFraction)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(EcoGreenPrimary, EcoGreenLight)
                    )
                )
        )

        Text(
            text = dato.dia.takeLast(2),
            fontSize = 10.sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
fun ImpactoAmbientalCard(estadisticas: EstadisticasDetalladas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImpactoItem(
                icon = "☁️",
                title = "CO₂ Evitado",
                value = "${String.format("%.1f", estadisticas.impactoAmbiental.co2Evitado)} kg",
                description = "Equivalente a ${(estadisticas.impactoAmbiental.co2Evitado / 21).roundToInt()} árboles absorbiendo CO₂ por un día",
                color = Color(0xFF4CAF50)
            )

            HorizontalDivider()

            ImpactoItem(
                icon = "🌳",
                title = "Árboles Equivalentes",
                value = "${estadisticas.impactoAmbiental.equivalencias.arboles}",
                description = "Has salvado el equivalente a plantar ${estadisticas.impactoAmbiental.equivalencias.arboles} árboles",
                color = Color(0xFF8BC34A)
            )

            HorizontalDivider()

            ImpactoItem(
                icon = "⚡",
                title = "Energía Ahorrada",
                value = "${String.format("%.1f", estadisticas.impactoAmbiental.equivalencias.energia)} kWh",
                description = "Suficiente para alimentar una casa por ${(estadisticas.impactoAmbiental.equivalencias.energia / 30).roundToInt()} días",
                color = Color(0xFFFFC107)
            )

            HorizontalDivider()

            ImpactoItem(
                icon = "💧",
                title = "Agua Ahorrada",
                value = "${String.format("%.0f", estadisticas.impactoAmbiental.equivalencias.agua)} L",
                description = "Equivalente a ${(estadisticas.impactoAmbiental.equivalencias.agua / 200).roundToInt()} duchas de 10 minutos",
                color = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
fun ImpactoItem(
    icon: String,
    title: String,
    value: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 24.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF757575),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun ComparativasCard(estadisticas: EstadisticasDetalladas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tu Posición",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "Mejor que el ${estadisticas.comparativas.porcentajeSuperior.roundToInt()}% de usuarios",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(EcoGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "#${estadisticas.comparativas.tuPosicion}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "TOP",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RachasCard(estadisticas: EstadisticasDetalladas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF6B35), Color(0xFFF7931E))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔥", fontSize = 32.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Racha Actual",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "${estadisticas.rachas.rachaActual} días",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RachaStatItem(
                    label = "Mejor Racha",
                    value = "${estadisticas.rachas.mejorRacha}",
                    unit = "días"
                )
            }
        }
    }
}

@Composable
fun RachaStatItem(
    label: String,
    value: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6B35)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )
        Text(
            text = unit,
            fontSize = 11.sp,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
fun EmptyTendenciaMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "📊", fontSize = 48.sp)
        Text(
            text = "Sin datos de tendencia",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Text(
            text = "Empieza a reciclar para ver tu progreso",
            fontSize = 12.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )
    }
}

fun getMaterialColor(material: String): Color {
    return when (material.lowercase()) {
        "plástico", "plastico" -> PlasticoColor
        "papel", "cartón", "carton" -> PapelColor
        "vidrio" -> VidrioColor
        "metal", "aluminio" -> MetalColor
        else -> Color(0xFF9E9E9E)
    }
}