package com.ecocoins.campus.presentation.recompensas

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.LoadingDialog
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.components.SuccessDialog
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensaDetailScreen(
    recompensaId: Long,
    onNavigateBack: () -> Unit,
    viewModel: RecompensasViewModel = hiltViewModel()
) {
    val recompensa by viewModel.selectedRecompensa.observeAsState()
    val ecoCoins by viewModel.ecoCoins.observeAsState(0L)
    val canjeState by viewModel.canjeState.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(recompensaId) {
        viewModel.getRecompensaById(recompensaId)
    }

    LaunchedEffect(canjeState) {
        when (canjeState) {
            is Resource.Success -> {
                showSuccessDialog = true
            }
            is Resource.Error -> {
                errorMessage = (canjeState as Resource.Error).message ?: "Error al canjear"
                showErrorDialog = true
            }
            else -> {}
        }
    }

    LoadingDialog(
        isLoading = canjeState is Resource.Loading,
        message = "Procesando canje..."
    )

    SuccessDialog(
        showDialog = showSuccessDialog,
        title = "¡Canje Exitoso!",
        message = "Tu recompensa ha sido canjeada. Revisa tu historial para más detalles.",
        onDismiss = {
            showSuccessDialog = false
            viewModel.resetCanjeState()
            onNavigateBack()
        }
    )

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.resetCanjeState()
            },
            icon = {
                Icon(Icons.Default.Error, "Error", tint = StatusRejected)
            },
            title = { Text("Error de Canje") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = {
                    showErrorDialog = false
                    viewModel.resetCanjeState()
                }) {
                    Text("Entendido")
                }
            }
        )
    }

    if (showConfirmDialog && recompensa != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = {
                Icon(Icons.Default.CardGiftcard, "Canjear", tint = EcoOrange)
            },
            title = { Text("Confirmar Canje") },
            text = {
                Column {
                    Text("¿Deseas canjear esta recompensa?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = recompensa!!.nombre,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Costo: ${recompensa!!.costoEcoCoins} EcoCoins",
                        color = EcoOrange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tu saldo después: ${ecoCoins - recompensa!!.costoEcoCoins} EC",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de Recompensa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando recompensa...")
            }
            recompensa == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Recompensa no encontrada")
                }
            }
            else -> {
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
                            .height(300.dp)
                            .background(
                                if (recompensa!!.imagenUrl != null)
                                    MaterialTheme.colorScheme.surfaceVariant
                                else
                                    EcoOrangeLight.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (recompensa!!.imagenUrl != null) {
                            AsyncImage(
                                model = recompensa!!.imagenUrl,
                                contentDescription = recompensa!!.nombre,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = recompensa!!.nombre,
                                tint = EcoOrange,
                                modifier = Modifier.size(100.dp)
                            )
                        }

                        // Stock badge
                        if (recompensa!!.stock <= 5 && recompensa!!.stock > 0) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = StatusRejected
                            ) {
                                Text(
                                    text = "¡Solo ${recompensa!!.stock} disponibles!",
                                    color = BackgroundWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        if (recompensa!!.stock == 0) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.Center),
                                shape = RoundedCornerShape(12.dp),
                                color = StatusRejected
                            ) {
                                Text(
                                    text = "AGOTADO",
                                    color = BackgroundWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
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
                        // Título y categoría
                        Column {
                            Text(
                                text = recompensa!!.nombre,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EcoOrange.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = recompensa!!.categoria,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EcoOrange,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Precio
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = EcoOrange
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Precio",
                                        color = BackgroundWhite.copy(alpha = 0.8f),
                                        fontSize = 14.sp
                                    )
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "${recompensa!!.costoEcoCoins}",
                                            color = BackgroundWhite,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "EcoCoins",
                                            color = BackgroundWhite.copy(alpha = 0.9f),
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Tu saldo",
                                        color = BackgroundWhite.copy(alpha = 0.8f),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "$ecoCoins EC",
                                        color = BackgroundWhite,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Descripción
                        Column {
                            Text(
                                text = "Descripción",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recompensa!!.descripcion,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Términos y condiciones
                        if (recompensa!!.terminosCondiciones != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Info",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Términos y Condiciones",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = recompensa!!.terminosCondiciones!!,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón de canje
                        val canAfford = ecoCoins >= recompensa!!.costoEcoCoins
                        val hasStock = recompensa!!.stock > 0

                        CustomButton(
                            text = when {
                                !hasStock -> "Agotado"
                                !canAfford -> "Saldo Insuficiente"
                                else -> "Canjear Ahora"
                            },
                            onClick = { showConfirmDialog = true },
                            enabled = canAfford && hasStock
                        )

                        if (!canAfford && hasStock) {
                            Text(
                                text = "Te faltan ${recompensa!!.costoEcoCoins - ecoCoins} EcoCoins",
                                fontSize = 12.sp,
                                color = StatusRejected,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}