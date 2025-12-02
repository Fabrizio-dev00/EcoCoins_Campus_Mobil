package com.ecocoins.campus.presentation.referidos

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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.ReferidoItem
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.CustomTextField
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.toShortDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferidosScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReferidosViewModel = hiltViewModel()
) {
    val referidosInfo by viewModel.referidosInfo.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    var codigoParaUsar by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Programa de Referidos") },
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
                LoadingState(message = "Cargando información...")
            }
            referidosInfo == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información del programa
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = EcoOrangeLight.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = EcoOrange
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "¡Invita a tus amigos y gana 50 EcoCoins por cada referido!",
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Resumen
                    item {
                        ReferidosResumenCard(referidosInfo!!)
                    }

                    // Tu código de referido
                    item {
                        Text(
                            text = "Tu Código de Referido",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = referidosInfo!!.codigoReferido,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary,
                                    letterSpacing = 4.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(referidosInfo!!.codigoReferido))
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EcoGreenPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.ContentCopy, "Copiar")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Copiar Código")
                                }
                            }
                        }
                    }

                    // Usar código de referido
                    item {
                        Text(
                            text = "¿Tienes un Código de Referido?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                CustomTextField(
                                    value = codigoParaUsar,
                                    onValueChange = { codigoParaUsar = it.uppercase() },
                                    label = "Código de Referido",
                                    placeholder = "Ingresa el código",
                                    minLines = 3
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                CustomButton(
                                    text = "Aplicar Código",
                                    onClick = {
                                        if (codigoParaUsar.isNotBlank()) {
                                            viewModel.usarCodigoReferido(codigoParaUsar)
                                        }
                                    },
                                    enabled = codigoParaUsar.isNotBlank()
                                )
                            }
                        }
                    }

                    // Lista de referidos
                    if (referidosInfo!!.referidos.isNotEmpty()) {
                        item {
                            Text(
                                text = "Tus Referidos (${referidosInfo!!.totalReferidos})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(referidosInfo!!.referidos) { referido ->
                            ReferidoCard(referido)
                        }
                    } else {
                        item {
                            EmptyState(
                                icon = Icons.Default.People,
                                title = "Sin referidos aún",
                                message = "Comparte tu código con amigos para ganar EcoCoins"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferidosResumenCard(info: com.ecocoins.campus.data.model.ReferidosInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ResumenItem(
                value = "${info.totalReferidos}",
                label = "Referidos",
                color = BackgroundWhite
            )
            ResumenItem(
                value = "+${info.ecoCoinsGanados}",
                label = "EcoCoins",
                color = BackgroundWhite
            )
        }
    }
}

@Composable
private fun ResumenItem(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ReferidoCard(referido: ReferidoItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = EcoGreenPrimary.copy(alpha = 0.2f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = referido.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Registrado el ${referido.fechaRegistro.toShortDate()}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${referido.recompensaObtenida}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoOrange
                )
                Text(
                    text = "EcoCoins",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}