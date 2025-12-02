package com.ecocoins.campus.presentation.scanner

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.LoadingDialog
import com.ecocoins.campus.ui.components.SuccessDialog
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIValidationScreen(
    onValidationComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val isValidating by viewModel.isValidating.observeAsState(false)
    val validationResult by viewModel.validationResult.observeAsState()
    val registrarState by viewModel.registrarState.observeAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Iniciar validación automáticamente
    LaunchedEffect(Unit) {
        viewModel.validateWithAI()
    }

    // Observar resultado de registro
    LaunchedEffect(registrarState) {
        when (registrarState) {
            is Resource.Success -> {
                val reciclaje = (registrarState as Resource.Success).data
                successMessage = "¡Reciclaje registrado!\nGanaste ${reciclaje?.ecoCoinsGanados} EcoCoins"
                showSuccessDialog = true
            }
            is Resource.Error -> {
                // Mostrar error
            }
            else -> {}
        }
    }

    LoadingDialog(
        isLoading = registrarState is Resource.Loading,
        message = "Registrando reciclaje..."
    )

    SuccessDialog(
        showDialog = showSuccessDialog,
        title = "¡Éxito!",
        message = successMessage,
        onDismiss = {
            showSuccessDialog = false
            viewModel.resetRegistrarState()
            onValidationComplete()
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Validación IA") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isValidating -> {
                    ValidatingAnimation()
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Validando con IA...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Analizando el material reciclable",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                validationResult == true -> {
                    ValidationSuccess(
                        onConfirm = {
                            viewModel.registrarReciclaje()
                        }
                    )
                }
                validationResult == false -> {
                    ValidationFailure(
                        onRetry = {
                            viewModel.resetScannerFlow()
                            onNavigateBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ValidatingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "validating")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(EcoGreenPrimary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "IA",
            tint = EcoGreenPrimary,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
private fun ValidationSuccess(onConfirm: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(EcoGreenPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Éxito",
                tint = EcoGreenPrimary,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Validación Exitosa!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = EcoGreenPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "El material ha sido identificado correctamente",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = EcoGreenLight.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                ValidationDetailRow("Material", "PLÁSTICO")
                Spacer(modifier = Modifier.height(8.dp))
                ValidationDetailRow("Peso", "2.5 kg")
                Spacer(modifier = Modifier.height(8.dp))
                ValidationDetailRow("EcoCoins", "+25 EC")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(
            text = "Confirmar Reciclaje",
            onClick = onConfirm
        )
    }
}

@Composable
private fun ValidationDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ValidationFailure(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(StatusRejected.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = "Error",
                tint = StatusRejected,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Validación Fallida",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = StatusRejected
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No se pudo identificar el material correctamente",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomButton(
            text = "Intentar de Nuevo",
            onClick = onRetry
        )
    }
}