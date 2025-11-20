package com.miempresa.ecocoinscampus.presentation.recompensas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecompensasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCanjeDialog by remember { mutableStateOf(false) }
    var selectedRecompensa by remember { mutableStateOf<com.miempresa.ecocoinscampus.data.model.Recompensa?>(null) }

    // Mostrar mensaje de éxito
    LaunchedEffect(uiState.canjeExitoso) {
        if (uiState.canjeExitoso) {
            // El mensaje se mostrará en el Snackbar
            kotlinx.coroutines.delay(3000)
            viewModel.resetCanjeExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recompensas") },
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
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
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
                    if (uiState.recompensas.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            EmptyRecompensasState()
                        }
                    } else {
                        items(uiState.recompensas) { recompensa ->
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
            if (uiState.canjeExitoso && uiState.canjeMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Text(uiState.canjeMessage ?: "Canje exitoso")
                    }
                }
            }

            // Mensaje de error
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
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

    // Diálogo de confirmación de canje
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
    recompensa: com.miempresa.ecocoinscampus.data.model.Recompensa,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        enabled = recompensa.disponible && recompensa.stock > 0
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icono
            Icon(
                Icons.Default.Redeem,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = if (recompensa.disponible && recompensa.stock > 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )

            // Nombre
            Text(
                recompensa.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Descripción
            Text(
                recompensa.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            // Costo
            Surface(
                color = if (recompensa.disponible && recompensa.stock > 0)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Paid,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "${recompensa.costoEcocoins}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Stock
            Text(
                if (recompensa.stock > 0) "Stock: ${recompensa.stock}"
                else "Agotado",
                style = MaterialTheme.typography.bodySmall,
                color = if (recompensa.stock > 0)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanjeDialog(
    recompensa: com.miempresa.ecocoinscampus.data.model.Recompensa,
    onDismiss: () -> Unit,
    onConfirm: (String?, String?) -> Unit
) {
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Redeem,
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
                // Información de la recompensa
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            recompensa.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Costo:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
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

                // Campos opcionales
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
fun EmptyRecompensasState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Redeem,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "No hay recompensas disponibles",
            style = MaterialTheme.typography.titleMedium,
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