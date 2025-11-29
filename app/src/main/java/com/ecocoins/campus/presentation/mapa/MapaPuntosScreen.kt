package com.ecocoins.campus.presentation.mapa

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.EstadoPunto
import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.data.model.TipoPuntoReciclaje

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaPuntosScreen(
    onNavigateBack: () -> Unit,
    viewModel: MapaPuntosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPunto by remember { mutableStateOf<PuntoReciclaje?>(null) }
    var showFiltros by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPuntos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Puntos de Reciclaje 🗺️",
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
                    IconButton(onClick = { showFiltros = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = EcoGreenPrimary
                        )
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Vista del mapa (simulada)
            MapaSimuladoView(
                puntos = uiState.puntos,
                onPuntoClick = { selectedPunto = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            // Lista de puntos
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EcoGreenPrimary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Puntos Cercanos (${uiState.puntos.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )

                            TextButton(onClick = { /* TODO: Ordenar */ }) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Sort,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text("Ordenar", fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    if (uiState.puntos.isEmpty()) {
                        item {
                            EmptyPuntosState()
                        }
                    } else {
                        items(uiState.puntos) { punto ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                            ) {
                                PuntoReciclajeCard(
                                    punto = punto,
                                    onClick = { selectedPunto = it },
                                    onNavigate = { /* TODO: Abrir navegación */ }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Diálogo de detalle
        selectedPunto?.let { punto ->
            PuntoDetalleDialog(
                punto = punto,
                onDismiss = { selectedPunto = null },
                onNavigate = { /* TODO: Navegar */ }
            )
        }

        // Bottom sheet de filtros
        if (showFiltros) {
            ModalBottomSheet(
                onDismissRequest = { showFiltros = false },
                containerColor = Color.White
            ) {
                FiltrosContent(
                    onAplicar = {
                        showFiltros = false
                        // TODO: Aplicar filtros
                    }
                )
            }
        }
    }
}

@Composable
fun MapaSimuladoView(
    puntos: List<PuntoReciclaje>,
    onPuntoClick: (PuntoReciclaje) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFFE0E0E0))
    ) {
        // Simulación de mapa con gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB3E5FC),
                            Color(0xFF81D4FA),
                            Color(0xFF4FC3F7)
                        )
                    )
                )
        )

        // Puntos simulados en el mapa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Punto central (Universidad)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Puntos cercanos simulados
            puntos.take(5).forEachIndexed { index, punto ->
                val offset = when (index) {
                    0 -> Alignment.TopStart
                    1 -> Alignment.TopEnd
                    2 -> Alignment.BottomStart
                    3 -> Alignment.BottomEnd
                    else -> Alignment.CenterEnd
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(offset)
                        .clickable { onPuntoClick(punto) }
                        .clip(CircleShape)
                        .background(getTipoColor(punto.tipo)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getTipoIcon(punto.tipo),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Indicador de ubicación actual
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Campus Universitario",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
            }
        }

        // Leyenda
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White.copy(alpha = 0.9f),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Leyenda",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF757575)
                )
                LeyendaItem(
                    color = Color(0xFFE53935),
                    texto = "Tú",
                    icono = Icons.Default.MyLocation
                )
                LeyendaItem(
                    color = EcoGreenPrimary,
                    texto = "Puntos",
                    icono = Icons.Default.Recycling
                )
            }
        }
    }
}

@Composable
fun LeyendaItem(
    color: Color,
    texto: String,
    icono: ImageVector
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(8.dp)
            )
        }
        Text(
            text = texto,
            fontSize = 10.sp,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun PuntoReciclajeCard(
    punto: PuntoReciclaje,
    onClick: (PuntoReciclaje) -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(punto) },
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(getTipoColor(punto.tipo).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getTipoIcon(punto.tipo),
                            contentDescription = null,
                            tint = getTipoColor(punto.tipo),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = punto.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF757575),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${String.format("%.1f", punto.distanciaKm)} km",
                                fontSize = 13.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                }

                // Estado badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getEstadoColor(punto.estado).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = getEstadoTexto(punto.estado),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = getEstadoColor(punto.estado),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Dirección
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = punto.direccion,
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                    lineHeight = 18.sp
                )
            }

            // Materiales aceptados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Recycling,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = punto.materialesAceptados.joinToString(", "),
                    fontSize = 12.sp,
                    color = EcoGreenPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Divider()

            // Footer con horario y navegación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = punto.horario,
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }

                TextButton(
                    onClick = onNavigate,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = EcoGreenPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Navegar", fontSize = 14.sp)
                }
            }

            // Capacidad (si aplica)
            if (punto.capacidadActual > 0) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Capacidad",
                            fontSize = 11.sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = "${punto.capacidadActual}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                punto.capacidadActual >= 80 -> Color(0xFFE53935)
                                punto.capacidadActual >= 50 -> EcoOrange
                                else -> EcoGreenPrimary
                            }
                        )
                    }

                    LinearProgressIndicator(
                        progress = { punto.capacidadActual / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = when {
                            punto.capacidadActual >= 80 -> Color(0xFFE53935)
                            punto.capacidadActual >= 50 -> EcoOrange
                            else -> EcoGreenPrimary
                        },
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
            }
        }
    }
}

@Composable
fun PuntoDetalleDialog(
    punto: PuntoReciclaje,
    onDismiss: () -> Unit,
    onNavigate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = punto.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getTipoColor(punto.tipo).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = getTipoTexto(punto.tipo),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = getTipoColor(punto.tipo),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dirección
                DetalleRow(
                    icon = Icons.Default.Place,
                    label = "Dirección",
                    value = punto.direccion
                )

                // Distancia
                DetalleRow(
                    icon = Icons.Default.LocationOn,
                    label = "Distancia",
                    value = "${String.format("%.1f", punto.distanciaKm)} km"
                )

                // Horario
                DetalleRow(
                    icon = Icons.Default.Schedule,
                    label = "Horario",
                    value = punto.horario
                )

                // Materiales
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Recycling,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Materiales aceptados",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                    }

                    punto.materialesAceptados.forEach { material ->
                        Text(
                            text = "• $material",
                            fontSize = 13.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier.padding(start = 28.dp)
                        )
                    }
                }

                // Teléfono
                punto.telefono?.let { telefono ->
                    DetalleRow(
                        icon = Icons.Default.Phone,
                        label = "Teléfono",
                        value = telefono
                    )
                }

                // Estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estado actual",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getEstadoColor(punto.estado).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = getEstadoTexto(punto.estado),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = getEstadoColor(punto.estado),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onNavigate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                )
            ) {
                Icon(
                    Icons.Default.Directions,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cómo llegar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun DetalleRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(20.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
        }
    }
}

@Composable
fun FiltrosContent(
    onAplicar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Filtros",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        // Tipos de punto
        Text(
            text = "Tipo de punto",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF757575)
        )

        TipoPuntoReciclaje.values().forEach { tipo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = true, // TODO: Estado
                    onCheckedChange = { }
                )
                Icon(
                    imageVector = getTipoIcon(tipo),
                    contentDescription = null,
                    tint = getTipoColor(tipo),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = getTipoTexto(tipo),
                    fontSize = 14.sp,
                    color = Color(0xFF212121)
                )
            }
        }

        Divider()

        // Solo abiertos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Solo puntos abiertos",
                fontSize = 14.sp,
                color = Color(0xFF212121)
            )
            Switch(
                checked = false, // TODO: Estado
                onCheckedChange = { },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EcoGreenPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { /* TODO: Limpiar */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpiar")
            }

            Button(
                onClick = onAplicar,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                )
            ) {
                Text("Aplicar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun EmptyPuntosState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFE0E0E0)
            )

            Text(
                text = "No se encontraron puntos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Text(
                text = "Intenta ajustar los filtros o ampliar el radio de búsqueda",
                fontSize = 14.sp,
                color = Color(0xFF757575),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Helper functions
fun getTipoIcon(tipo: TipoPuntoReciclaje): ImageVector {
    return when (tipo) {
        TipoPuntoReciclaje.CONTENEDOR -> Icons.Default.Delete
        TipoPuntoReciclaje.CENTRO_ACOPIO -> Icons.Default.Warehouse
        TipoPuntoReciclaje.PUNTO_LIMPIO -> Icons.Default.CleaningServices
        TipoPuntoReciclaje.UNIVERSIDAD -> Icons.Default.School
    }
}

fun getTipoColor(tipo: TipoPuntoReciclaje): Color {
    return when (tipo) {
        TipoPuntoReciclaje.CONTENEDOR -> Color(0xFF2196F3)
        TipoPuntoReciclaje.CENTRO_ACOPIO -> Color(0xFF4CAF50)
        TipoPuntoReciclaje.PUNTO_LIMPIO -> Color(0xFF9C27B0)
        TipoPuntoReciclaje.UNIVERSIDAD -> Color(0xFFFF9800)
    }
}

fun getTipoTexto(tipo: TipoPuntoReciclaje): String {
    return when (tipo) {
        TipoPuntoReciclaje.CONTENEDOR -> "Contenedor"
        TipoPuntoReciclaje.CENTRO_ACOPIO -> "Centro de Acopio"
        TipoPuntoReciclaje.PUNTO_LIMPIO -> "Punto Limpio"
        TipoPuntoReciclaje.UNIVERSIDAD -> "Universidad"
    }
}

fun getEstadoColor(estado: EstadoPunto): Color {
    return when (estado) {
        EstadoPunto.ABIERTO -> Color(0xFF4CAF50)
        EstadoPunto.CERRADO -> Color(0xFF757575)
        EstadoPunto.LLENO -> Color(0xFFE53935)
    }
}

fun getEstadoTexto(estado: EstadoPunto): String {
    return when (estado) {
        EstadoPunto.ABIERTO -> "ABIERTO"
        EstadoPunto.CERRADO -> "CERRADO"
        EstadoPunto.LLENO -> "LLENO"
    }
}