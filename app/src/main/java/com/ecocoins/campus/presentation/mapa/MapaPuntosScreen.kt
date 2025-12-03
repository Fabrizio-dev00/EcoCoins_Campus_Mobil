package com.ecocoins.campus.presentation.mapa

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapaPuntosScreen(
    onNavigateBack: () -> Unit,
    viewModel: MapaPuntosViewModel = hiltViewModel()
) {
    val puntos by viewModel.puntos.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val selectedPunto by viewModel.selectedPunto.observeAsState()

    var showDetailsDialog by remember { mutableStateOf(false) }

    // Permiso de ubicación con Accompanist
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        viewModel.loadPuntos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Puntos de Reciclaje") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadPuntos() }) {
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
            // Solicitar permiso de ubicación
            if (!locationPermissionState.status.isGranted) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EcoOrange.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            tint = EcoOrange,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Activa tu ubicación",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Para mostrarte puntos cercanos",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                            Text("Activar")
                        }
                    }
                }
            }

            // Card de información
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EcoGreenLight.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = EcoGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encuentra los puntos de reciclaje más cercanos a tu ubicación",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when {
                isLoading -> {
                    LoadingState(message = "Cargando puntos...")
                }
                puntos.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.LocationOff,
                        title = "Sin puntos cercanos",
                        message = "No hay puntos de reciclaje disponibles en este momento"
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(puntos) { punto ->
                            PuntoReciclajeCard(
                                punto = punto,
                                onClick = {
                                    viewModel.selectPunto(punto)
                                    showDetailsDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dialog de detalles
        if (showDetailsDialog && selectedPunto != null) {
            AlertDialog(
                onDismissRequest = { showDetailsDialog = false },
                title = { Text(selectedPunto!!.nombre) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailRow(
                            icon = Icons.Default.LocationOn,
                            label = "Dirección",
                            value = selectedPunto!!.direccion
                        )
                        DetailRow(
                            icon = Icons.Default.AccessTime,
                            label = "Horario",
                            value = selectedPunto!!.horario
                        )
                        DetailRow(
                            icon = Icons.Default.Recycling,
                            label = "Materiales",
                            value = selectedPunto!!.materialesAceptados.joinToString(", ")
                        )
                        selectedPunto!!.distanciaKm?.let { distancia ->
                            DetailRow(
                                icon = Icons.Default.DirectionsWalk,
                                label = "Distancia",
                                value = String.format("%.1f km", distancia)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showDetailsDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

@Composable
private fun PuntoReciclajeCard(
    punto: PuntoReciclaje,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Nombre y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = punto.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EstadoBadge(punto.estado)
            }

            // Dirección
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Dirección",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = punto.direccion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Horario y distancia
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Horario",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = punto.horario,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                punto.distanciaKm?.let { distancia ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = "Distancia",
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f km", distancia),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = EcoGreenPrimary
                        )
                    }
                }
            }

            // Materiales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                punto.materialesAceptados.take(3).forEach { material ->
                    MaterialChip(material)
                }
                if (punto.materialesAceptados.size > 3) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "+${punto.materialesAceptados.size - 3}",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoBadge(estado: String) {
    val (color, text) = when (estado) {
        "DISPONIBLE" -> EcoGreenPrimary to "Disponible"
        "LLENO" -> StatusRejected to "Lleno"
        "MANTENIMIENTO" -> StatusPending to "Mantenimiento"
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

@Composable
private fun MaterialChip(material: String) {
    val color = when (material.uppercase()) {
        "PLASTICO" -> PlasticBlue
        "PAPEL" -> PaperBrown
        "VIDRIO" -> GlassGreen
        "METAL" -> MetalGray
        else -> EcoGreenSecondary
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = material,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = EcoGreenPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}