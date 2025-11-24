package com.ecocoins.campus.presentation.recompensas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.Recompensa
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecompensasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCanjeDialog by remember { mutableStateOf(false) }
    var selectedRecompensa by remember { mutableStateOf<Recompensa?>(null) }

    LaunchedEffect(uiState.canjeExitoso) {
        if (uiState.canjeExitoso) {
            kotlinx.coroutines.delay(3000)
            viewModel.resetCanjeExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Recompensas",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
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
                        CircularProgressIndicator()
                        Text(
                            "Cargando recompensas...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (uiState.recompensas.isEmpty()) {
                EmptyRecompensasState(modifier = Modifier.padding(padding))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.recompensas,
                        key = { it.id }
                    ) { recompensa ->
                        AnimatedVisibility(
                            visible = true,
                            enter = scaleIn() + fadeIn(),
                            exit = scaleOut() + fadeOut()
                        ) {
                            RecompensaCard(
                                recompensa = recompensa,
                                onClick = {
                                    selectedRecompensa = recompensa
                                    showCanjeDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // Mensaje de éxito
            AnimatedVisibility(
                visible = uiState.canjeExitoso && uiState.canjeMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            uiState.canjeMessage ?: "Canje exitoso",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Mensaje de error
            uiState.error?.let { error ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Snackbar(
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

    if (showCanjeDialog && selectedRecompensa != null) {
        CanjeDialog(
            recompensa = selectedRecompensa!!,
            onDismiss = {
                showCanjeDialog = false
                selectedRecompensa = null
            },
            onConfirm = { direccion, telefono ->
                viewModel.canjearRecompensa(
                    selectedRecompensa!!.id,
                    direccion,
                    telefono
                )
                showCanjeDialog = false
                selectedRecompensa = null
            }
        )
    }
}

@Composable
fun RecompensaCard(
    recompensa: Recompensa,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press"
    )

    Card(
        onClick = {
            isPressed = true
            kotlinx.coroutines.GlobalScope.launch {
                kotlinx.coroutines.delay(100)
                isPressed = false
                onClick()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .scale(scale),
        enabled = recompensa.disponible && recompensa.stock > 0,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (recompensa.disponible && recompensa.stock > 0)
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        else
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Redeem,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = if (recompensa.disponible && recompensa.stock > 0)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    recompensa.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    recompensa.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                color = if (recompensa.disponible && recompensa.stock > 0)
                    MaterialTheme.colorScheme.tertiaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Paid,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "${recompensa.costoEcocoins}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                if (recompensa.stock > 0) "Stock: ${recompensa.stock}"
                else "Agotado",
                style = MaterialTheme.typography.bodySmall,
                color = if (recompensa.stock > 0)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.error,
                fontWeight = if (recompensa.stock <= 0) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanjeDialog(
    recompensa: Recompensa,
    onDismiss: () -> Unit,
    onConfirm: (String?, String?) -> Unit
) {
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.CardGiftcard,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                "Canjear ${recompensa.nombre}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            recompensa.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Costo:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Paid,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "${recompensa.costoEcocoins} EcoCoins",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección de entrega (opcional)") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono de contacto (opcional)") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    "Nos pondremos en contacto contigo para coordinar la entrega.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        direccion.ifBlank { null },
                        telefono.ifBlank { null }
                    )
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar Canje")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EmptyRecompensasState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CardGiftcard,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "No hay recompensas disponibles",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Vuelve pronto para ver nuevas recompensas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}