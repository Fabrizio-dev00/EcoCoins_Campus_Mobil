package com.ecocoins.campus.presentation.estadisticas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import com.ecocoins.campus.data.model.EstadisticaMaterial
import com.ecocoins.campus.data.model.DatoTendencia
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
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEstadisticas()
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EcoGreenPrimary)
            }
        } else {
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
                        ResumenGeneralCard(uiState = uiState)
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
                                materiales = uiState.porTipoMaterial
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
                                tendencias = uiState.tendenciaSemanal
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

                            ImpactoAmbientalCard(uiState = uiState)
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

                            ComparativasCard(uiState = uiState)
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

                            RachasCard(uiState = uiState)
                        }
                    }
                }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Error
        uiState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.BottomCenter
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

@Composable
fun ResumenGeneralCard(uiState: EstadisticasUiState) {
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
                    value = "${uiState.totalReciclajes}",
                    label = "Reciclajes",
                    modifier = Modifier.weight(1f)
                )
                ResumenStatItem(
                    icon = Icons.Default.Scale,
                    value = String.format("%.1f", uiState.totalKgReciclados),
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
                    value = "${uiState.totalEcoCoinsGanados}",
                    label = "Ganados",
                    modifier = Modifier.weight(1f)
                )
                ResumenStatItem(
                    icon = Icons.Default.ShoppingCart,
                    value = "${uiState.totalEcoCoinsGastados}",
                    label = "Gastados",
                    modifier = Modifier.weight(1f)
                )
            }

            Divider(color = Color.White.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saldo Actual",
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
                            text = "${uiState.saldoActual}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "EcoCoins",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
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
fun MaterialesDistribucionCard(
    materiales: List<EstadisticaMaterial>
) {
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
            // Gráfico de barras circular (simulado con barras de progreso)
            materiales.forEach { material ->
                MaterialProgressBar(material = material)
            }

            Divider()

            // Detalles numéricos
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
                        text = String.format("%.1f kg", materiales.sumOf { it.pesoTotal }),
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
                        text = "${materiales.sumOf { it.ecoCoinsGanados }}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                    Text(
                        text = "EcoCoins",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }
    }
}

@Composable
fun MaterialProgressBar(material: EstadisticaMaterial) {
    val color = getMaterialColor(material.tipoMaterial)

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
                    text = material.tipoMaterial,
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
            progress = { material.porcentaje / 100f },
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
                text = "${String.format("%.1f", material.pesoTotal)} kg",
                fontSize = 11.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun TendenciaSemanolCard(
    tendencias: List<DatoTendencia>
) {
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
            // Gráfico de barras simple
            if (tendencias.isNotEmpty()) {
                val maxCantidad = tendencias.maxOf { it.cantidad }

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

                Divider()

                // Estadísticas de la semana
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${tendencias.sumOf { it.cantidad }}",
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

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%.1f", tendencias.sumOf { it.pesoKg }),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text(
                            text = "Kg",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${tendencias.sumOf { it.ecoCoins }}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoOrange
                        )
                        Text(
                            text = "EcoCoins",
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
    dato: DatoTendencia,
    maxCantidad: Int,
    modifier: Modifier = Modifier
) {
    val heightFraction = if (maxCantidad > 0) {
        (dato.cantidad.toFloat() / maxCantidad.toFloat()).coerceIn(0.1f, 1f)
    } else 0.1f

    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Valor
        if (dato.cantidad > 0) {
            Text(
                text = "${dato.cantidad}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary
            )
        }

        // Barra
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

        // Día
        Text(
            text = dato.fecha.takeLast(2), // Últimos 2 caracteres (día)
            fontSize = 10.sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
fun ImpactoAmbientalCard(uiState: EstadisticasUiState) {
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
                title = "CO₂ Ahorrado",
                value = "${String.format("%.1f", uiState.co2Ahorrado)} kg",
                description = "Equivalente a ${(uiState.co2Ahorrado / 21).roundToInt()} árboles absorbiendo CO₂ por un día",
                color = Color(0xFF4CAF50)
            )

            Divider()

            ImpactoItem(
                icon = "🌳",
                title = "Árboles Equivalentes",
                value = "${uiState.arbolesEquivalentes}",
                description = "Has salvado el equivalente a plantar ${uiState.arbolesEquivalentes} árboles",
                color = Color(0xFF8BC34A)
            )

            Divider()

            ImpactoItem(
                icon = "⚡",
                title = "Energía Ahorrada",
                value = "${String.format("%.1f", uiState.energiaAhorrada)} kWh",
                description = "Suficiente para alimentar una casa por ${(uiState.energiaAhorrada / 30).roundToInt()} días",
                color = Color(0xFFFFC107)
            )

            Divider()

            ImpactoItem(
                icon = "💧",
                title = "Agua Ahorrada",
                value = "${String.format("%.0f", uiState.aguaAhorrada)} L",
                description = "Equivalente a ${(uiState.aguaAhorrada / 200).roundToInt()} duchas de 10 minutos",
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
            Text(
                text = icon,
                fontSize = 24.sp
            )
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
fun ComparativasCard(uiState: EstadisticasUiState) {
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
            // Tu rendimiento vs promedio
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tu Rendimiento vs Promedio",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tú: ${String.format("%.1f", uiState.tuRendimiento)} kg",
                            fontSize = 13.sp,
                            color = EcoGreenPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Promedio: ${String.format("%.1f", uiState.promedioUniversidad)} kg",
                            fontSize = 13.sp,
                            color = Color(0xFF757575)
                        )
                    }

                    val diferencia = ((uiState.tuRendimiento / uiState.promedioUniversidad - 1) * 100).roundToInt()

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (diferencia >= 0) EcoGreenPrimary.copy(alpha = 0.1f) else Color(0xFFE53935).copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (diferencia >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (diferencia >= 0) EcoGreenPrimary else Color(0xFFE53935),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${if (diferencia >= 0) "+" else ""}$diferencia%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (diferencia >= 0) EcoGreenPrimary else Color(0xFFE53935)
                            )
                        }
                    }
                }

                // Barra de comparación
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFFE0E0E0))
                ) {
                    val yourProgress = (uiState.tuRendimiento / (uiState.tuRendimiento + uiState.promedioUniversidad)).coerceIn(0.0, 1.0)

                    Row(modifier = Modifier.fillMaxSize()) {
                        // Tu parte
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(yourProgress.toFloat())
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(EcoGreenPrimary, EcoGreenLight)
                                    ),
                                    shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (yourProgress > 0.2) {
                                Text(
                                    text = "Tú",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Parte del promedio
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (yourProgress < 0.8) {
                                Text(
                                    text = "Promedio",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF757575)
                                )
                            }
                        }
                    }
                }
            }

            Divider()

            // Posición general
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tu Posición General",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "Mejor que el ${uiState.mejorQuePorc}% de usuarios",
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
                            text = "#${uiState.posicionGeneral}",
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
fun RachasCard(uiState: EstadisticasUiState) {
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
            // Racha actual
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
                    Text(
                        text = "🔥",
                        fontSize = 32.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Racha Actual",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "${uiState.rachaActual} días",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }
            }

            Divider()

            // Estadísticas de rachas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RachaStatItem(
                    label = "Mejor Racha",
                    value = "${uiState.mejorRacha}",
                    unit = "días"
                )

                VerticalDivider(modifier = Modifier.height(50.dp))

                RachaStatItem(
                    label = "Días Activos",
                    value = "${uiState.diasTotales}",
                    unit = "total"
                )
            }

            // Último reciclaje
            if (uiState.ultimoReciclaje != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoGreenPrimary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Último Reciclaje",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                            Text(
                                text = uiState.ultimoReciclaje,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = EcoGreenPrimary
                            )
                        }
                    }
                }
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
        Text(
            text = "📊",
            fontSize = 48.sp
        )
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

@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(1.dp)
            .background(Color(0xFFE0E0E0))
    )
}

fun getMaterialColor(tipoMaterial: String): Color {
    return when (tipoMaterial.lowercase()) {
        "plástico", "plastico" -> PlasticoColor
        "papel", "cartón", "carton" -> PapelColor
        "vidrio" -> VidrioColor
        "metal", "aluminio" -> MetalColor
        else -> Color(0xFF9E9E9E)
    }
}