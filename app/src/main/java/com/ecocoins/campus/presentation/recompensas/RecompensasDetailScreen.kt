package com.ecocoins.campus.presentation.recompensas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.ErrorDialog
import com.ecocoins.campus.ui.components.LoadingDialog
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasDetailScreen(
    recompensaId: Long,
    onNavigateBack: () -> Unit,
    viewModel: RecompensasViewModel = hiltViewModel()
) {
    val recompensa by viewModel.selectedRecompensa.observeAsState()
    val saldoEcoCoins by viewModel.ecoCoins.observeAsState(0)
    val canjeState by viewModel.canjeState.observeAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(recompensaId) {
        viewModel.getRecompensaById(recompensaId)
    }

    LaunchedEffect(canjeState) {
        when (val state = canjeState) {
            is Resource.Success -> {
                showSuccessDialog = true
                showConfirmDialog = false
            }
            is Resource.Error -> {
                errorMessage = state.message ?: "Error al canjear la recompensa"
                showErrorDialog = true
                showConfirmDialog = false
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Recompensa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        recompensa?.let { reward ->
            val hasStock = reward.stock > 0
            val canAfford = saldoEcoCoins >= reward.costoEcoCoins

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Imagen de la recompensa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (reward.imagenUrl != null) {
                        AsyncImage(
                            model = reward.imagenUrl,
                            contentDescription = reward.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = reward.nombre,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Badge de stock
                    if (!hasStock) {
                        Surface(
                            modifier = Modifier.align(Alignment.Center),
                            shape = RoundedCornerShape(12.dp),
                            color = StatusRejected.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "AGOTADO",
                                color = BackgroundWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                            )
                        }
                    } else if (reward.stock <= 5) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = EcoOrange.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "¡Solo ${reward.stock} disponibles!",
                                color = BackgroundWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título
                    Text(
                        text = reward.nombre,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Categoría
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EcoGreenPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = reward.categoria,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = EcoGreenPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Card de precio
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = EcoOrange.copy(alpha = 0.1f)
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
                                Text(
                                    text = "Precio:",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "EcoCoins",
                                        tint = EcoOrange,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${reward.costoEcoCoins}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoOrange
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Tu saldo:",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$saldoEcoCoins EC",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (canAfford) EcoGreenPrimary else StatusRejected
                                )
                            }
                        }
                    }

                    // Descripción
                    Text(
                        text = "Descripción",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = reward.descripcion,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Términos y condiciones
                    if (!reward.descripcion.contains("términos", ignoreCase = true)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Información",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Esta recompensa está sujeta a disponibilidad y puede requerir validación adicional.",
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Mensajes de advertencia
                    if (!canAfford) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = StatusRejected.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Advertencia",
                                    tint = StatusRejected,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Te faltan ${reward.costoEcoCoins - saldoEcoCoins} EcoCoins para canjear esta recompensa",
                                    fontSize = 14.sp,
                                    color = StatusRejected,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Botón de canje
                    CustomButton(
                        text = if (hasStock) "Canjear Ahora" else "Agotado",
                        onClick = { showConfirmDialog = true },
                        enabled = canAfford && hasStock,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Dialog de confirmación
            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("Confirmar Canje") },
                    text = {
                        Column {
                            Text("¿Estás seguro de canjear esta recompensa?")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• Recompensa: ${reward.nombre}")
                            Text("• Costo: ${reward.costoEcoCoins} EC")
                            Text("• Saldo después: ${saldoEcoCoins - reward.costoEcoCoins} EC")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.canjearRecompensa(recompensaId)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EcoOrange
                            )
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Success Dialog
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("¡Canje Exitoso!") },
                    text = { Text("Tu recompensa ha sido canjeada exitosamente. Recibirás instrucciones en tu correo.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onNavigateBack()
                            }
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }

            // Error Dialog
            if (showErrorDialog) {
                ErrorDialog(
                    message = errorMessage,
                    onDismiss = { showErrorDialog = false },
                    showDialog = TODO(),
                    title = TODO(),
                    onRetry = TODO()
                )
            }

            // Loading Dialog
            if (canjeState is Resource.Loading) {
                LoadingDialog(
                    message = "Procesando canje...",
                    isLoading = TODO()
                )
            }
        }
    }
}