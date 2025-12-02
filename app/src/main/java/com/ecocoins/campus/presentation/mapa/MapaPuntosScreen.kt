package com.ecocoins.campus.presentation.mapa

import android.Manifest
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
import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapaPuntosScreen(
    onNavigateBack: () -> Unit,
    viewModel: MapaPuntosViewModel = hiltViewModel()
) {
    val puntos by viewModel.puntos.observeAsState(emptyList())
    val selectedPunto by viewModel.selectedPunto.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    var showPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!locationPermissionState.hasPermission) {
            showPermissionDialog = true
        }
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
        when {
            isLoading -> {
                LoadingState(message = "Cargando puntos de reciclaje...")
            }
            error != null -> {
                ErrorState(
                    message = error ?: "Error desconocido",
                    onRetry = { viewModel.loadPuntos() }
                )
            }
            puntos.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.LocationOn,
                    title = "Sin puntos de reciclaje",
                    message = "No hay puntos disponibles en tu área"
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = EcoGreenLight.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = EcoGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Encuentra los puntos de reciclaje más cercanos a ti",
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Lista de puntos
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(puntos) { punto ->
                            PuntoReciclajeCard(
                                punto = punto,
                                onClick = { viewModel.selectPunto(punto) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de permiso de ubicación
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            icon = {
                Icon(Icons.Default.LocationOn, "Ubicación", tint = EcoGreenPrimary)
            },
            title = { Text("Permiso de Ubicación") },
            text = { Text("Para mostrarte los puntos de reciclaje más cercanos, necesitamos acceso a tu ubicación.") },
            confirmButton = {
                Button(
                    onClick = {
                        locationPermissionState.launchPermissionRequest()
                        showPermissionDialog = false
                    }
                ) {
                    Text("Permitir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Ahora no")
                }
            }
        )
    }

    // Diálogo de detalles del punto seleccionado
    selectedPunto?.let { punto ->
        AlertDialog(
            onDismissRequest = { viewModel.selectPunto(null) },
            title = { Text(punto.nombre) },
            text = {
                Column {
                    DetailRow(icon = Icons.Default.LocationOn, text = punto.direccion)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(icon = Icons.Default.AccessTime, text = punto.horario)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        icon = Icons.Default.RecyclingCenter,
                        text = "Materiales: ${punto.materialesAceptados.joinToString(", ")}"
                    )
                    if (punto.distanciaKm != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailRow(
                            icon = Icons.Default.DirectionsWalk,
                            text = "${String.format("%.1f", punto.distanciaKm)} km de distancia"
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.selectPunto(null) }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun PuntoReciclajeCard(
    punto: PuntoReciclaje,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
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
                        text = punto.nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = punto.direccion,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                EstadoBadge(punto.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Horario",
                        modifier = Modifier.size(16.dp),
                        tint = EcoGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = punto.horario,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (punto.distanciaKm != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = "Distancia",
                            modifier = Modifier.size(16.dp),
                            tint = PlasticBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${String.format("%.1f", punto.distanciaKm)} km",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PlasticBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Materiales aceptados
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
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
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MaterialChip(material: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = EcoGreenPrimary.copy(alpha = 0.2f)
    ) {
        Text(
            text = material,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = EcoGreenPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EstadoBadge(estado: String) {
    val (color, text) = when (estado.uppercase()) {
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
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = EcoGreenPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}