package com.ecocoins.campus.presentation.logros

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.CategoriaLogro
import com.ecocoins.campus.data.model.Logro
import com.ecocoins.campus.data.model.RarezaLogro

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF5F5F5)
private val GoldColor = Color(0xFFFFD700)

// Colores por rareza
private val ComunColor = Color(0xFF9E9E9E)
private val RaroColor = Color(0xFF2196F3)
private val EpicoColor = Color(0xFF9C27B0)
private val LegendarioColor = Color(0xFFFF9800)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogrosScreen(
    onNavigateBack: () -> Unit,
    viewModel: LogrosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategoria by remember { mutableStateOf<CategoriaLogro?>(null) }
    var selectedLogro by remember { mutableStateOf<Logro?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadLogros()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Logros 🏆",
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
                // Progreso general
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                    ) {
                        ProgresoGeneralCard(
                            totalDesbloqueados = uiState.totalDesbloqueados,
                            totalLogros = uiState.totalLogros,
                            porcentaje = uiState.porcentajeCompletado
                        )
                    }
                }

                // Filtro por categoría
                item {
                    CategoriaFilterChips(
                        selectedCategoria = selectedCategoria,
                        onCategoriaSelected = { selectedCategoria = it }
                    )
                }

                // Lista de logros filtrados
                val logrosFiltrados = if (selectedCategoria != null) {
                    uiState.logros.filter { it.categoria == selectedCategoria }
                } else {
                    uiState.logros
                }

                items(
                    items = logrosFiltrados,
                    key = { it.id }
                ) { logro ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                    ) {
                        LogroCard(
                            logro = logro,
                            onClick = { selectedLogro = it }
                        )
                    }
                }

                // Mensaje si no hay logros
                if (logrosFiltrados.isEmpty()) {
                    item {
                        EmptyLogrosMessage(categoria = selectedCategoria)
                    }
                }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Diálogo de detalle de logro
        selectedLogro?.let { logro ->
            LogroDetailDialog(
                logro = logro,
                onDismiss = { selectedLogro = null }
            )
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
fun ProgresoGeneralCard(
    totalDesbloqueados: Int,
    totalLogros: Int,
    porcentaje: Int
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progreso General",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "$totalDesbloqueados / $totalLogros",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Medalla animada
                val infiniteTransition = rememberInfiniteTransition(label = "medal")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = -5f,
                    targetValue = 5f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "rotation"
                )

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .rotate(rotation)
                        .clip(CircleShape)
                        .background(GoldColor.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 36.sp
                    )
                }
            }

            // Barra de progreso
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Completado",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "$porcentaje%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                LinearProgressIndicator(
                    progress = { porcentaje / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = GoldColor,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )
            }
        }
    }
}

@Composable
fun CategoriaFilterChips(
    selectedCategoria: CategoriaLogro?,
    onCategoriaSelected: (CategoriaLogro?) -> Unit
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filtrar por Categoría",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF757575)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chip "Todos"
                FilterChip(
                    selected = selectedCategoria == null,
                    onClick = { onCategoriaSelected(null) },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )

                // Chips de categorías
                CategoriaLogro.values().forEach { categoria ->
                    FilterChip(
                        selected = selectedCategoria == categoria,
                        onClick = { onCategoriaSelected(categoria) },
                        label = {
                            Text(
                                text = getCategoriaName(categoria),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = getCategoriaIcon(categoria),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EcoGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LogroCard(
    logro: Logro,
    onClick: (Logro) -> Unit
) {
    val rarezaColor = getRarezaColor(logro.rareza)
    val progreso = if (logro.objetivo > 0) {
        (logro.progreso.toFloat() / logro.objetivo.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(logro) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (logro.desbloqueado) Color.White else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (logro.desbloqueado) 2.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del logro
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (logro.desbloqueado) {
                            Brush.linearGradient(
                                colors = listOf(rarezaColor, rarezaColor.copy(alpha = 0.7f))
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE0E0E0),
                                    Color(0xFFBDBDBD)
                                )
                            )
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (logro.desbloqueado) rarezaColor else Color(0xFFBDBDBD),
                        shape = CircleShape
                    )
                    .alpha(if (logro.desbloqueado) 1f else 0.5f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getLogroEmoji(logro.categoria),
                    fontSize = 32.sp
                )

                // Checkmark si está desbloqueado
                if (logro.desbloqueado) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(EcoGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Info del logro
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = logro.nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (logro.desbloqueado) Color(0xFF212121) else Color(0xFF757575)
                    )

                    // Badge de rareza
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = rarezaColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = getRarezaName(logro.rareza),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = rarezaColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = logro.descripcion,
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                    maxLines = 2
                )

                // Barra de progreso
                if (!logro.desbloqueado) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = rarezaColor,
                            trackColor = Color(0xFFE0E0E0),
                        )
                        Text(
                            text = "${logro.progreso} / ${logro.objetivo}",
                            fontSize = 11.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                // Fecha de desbloqueo
                if (logro.desbloqueado && logro.fechaDesbloqueo != null) {
                    Text(
                        text = "Desbloqueado: ${logro.fechaDesbloqueo}",
                        fontSize = 11.sp,
                        color = EcoGreenPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Recompensa
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Paid,
                    contentDescription = null,
                    tint = EcoOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "+${logro.recompensaEcoCoins}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (logro.desbloqueado) EcoOrange else Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
fun LogroDetailDialog(
    logro: Logro,
    onDismiss: () -> Unit
) {
    val rarezaColor = getRarezaColor(logro.rareza)
    val progreso = if (logro.objetivo > 0) {
        (logro.progreso.toFloat() / logro.objetivo.toFloat()).coerceIn(0f, 1f)
    } else 0f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icono grande
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            if (logro.desbloqueado) {
                                Brush.radialGradient(
                                    colors = listOf(rarezaColor, rarezaColor.copy(alpha = 0.5f))
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                                )
                            }
                        )
                        .border(3.dp, rarezaColor, CircleShape)
                        .alpha(if (logro.desbloqueado) 1f else 0.5f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getLogroEmoji(logro.categoria),
                        fontSize = 48.sp
                    )
                }

                // Título
                Text(
                    text = logro.nombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    textAlign = TextAlign.Center
                )

                // Badge de rareza
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rarezaColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = getRarezaName(logro.rareza),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = rarezaColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Descripción
                Text(
                    text = logro.descripcion,
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )

                Divider()

                // Progreso
                if (!logro.desbloqueado) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = "${(progreso * 100).toInt()}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = rarezaColor
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = rarezaColor,
                            trackColor = Color(0xFFE0E0E0),
                        )

                        Text(
                            text = "${logro.progreso} / ${logro.objetivo}",
                            fontSize = 13.sp,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Fecha de desbloqueo
                if (logro.desbloqueado && logro.fechaDesbloqueo != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Desbloqueado el ${logro.fechaDesbloqueo}",
                            fontSize = 13.sp,
                            color = EcoGreenPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Recompensa
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = EcoOrange.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = null,
                            tint = EcoOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+${logro.recompensaEcoCoins} EcoCoins",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoOrange
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                )
            ) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun EmptyLogrosMessage(categoria: CategoriaLogro?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🔍",
                fontSize = 48.sp
            )
            Text(
                text = "No hay logros disponibles",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = if (categoria != null) {
                    "No hay logros en esta categoría"
                } else {
                    "¡Empieza a reciclar para desbloquear logros!"
                },
                fontSize = 14.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions
fun getCategoriaName(categoria: CategoriaLogro): String {
    return when (categoria) {
        CategoriaLogro.RECICLAJE -> "Reciclaje"
        CategoriaLogro.ECOCOINS -> "EcoCoins"
        CategoriaLogro.SOCIAL -> "Social"
        CategoriaLogro.RACHA -> "Rachas"
        CategoriaLogro.ESPECIAL -> "Especiales"
    }
}

fun getCategoriaIcon(categoria: CategoriaLogro): ImageVector {
    return when (categoria) {
        CategoriaLogro.RECICLAJE -> Icons.Default.Recycling
        CategoriaLogro.ECOCOINS -> Icons.Default.Paid
        CategoriaLogro.SOCIAL -> Icons.Default.People
        CategoriaLogro.RACHA -> Icons.Default.LocalFireDepartment
        CategoriaLogro.ESPECIAL -> Icons.Default.Star
    }
}

fun getLogroEmoji(categoria: CategoriaLogro): String {
    return when (categoria) {
        CategoriaLogro.RECICLAJE -> "♻️"
        CategoriaLogro.ECOCOINS -> "💰"
        CategoriaLogro.SOCIAL -> "👥"
        CategoriaLogro.RACHA -> "🔥"
        CategoriaLogro.ESPECIAL -> "⭐"
    }
}

fun getRarezaColor(rareza: RarezaLogro): Color {
    return when (rareza) {
        RarezaLogro.COMUN -> ComunColor
        RarezaLogro.RARO -> RaroColor
        RarezaLogro.EPICO -> EpicoColor
        RarezaLogro.LEGENDARIO -> LegendarioColor
    }
}

fun getRarezaName(rareza: RarezaLogro): String {
    return when (rareza) {
        RarezaLogro.COMUN -> "COMÚN"
        RarezaLogro.RARO -> "RARO"
        RarezaLogro.EPICO -> "ÉPICO"
        RarezaLogro.LEGENDARIO -> "LEGENDARIO"
    }
}