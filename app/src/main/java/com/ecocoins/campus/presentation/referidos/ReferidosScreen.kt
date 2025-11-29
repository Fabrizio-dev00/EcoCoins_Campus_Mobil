package com.ecocoins.campus.presentation.referidos

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferidosScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReferidosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var showCopiedSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadReferidos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Referir Amigos 👥",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de recompensa
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                ) {
                    RecompensaCard(
                        ecoCoinsGanados = uiState.totalEcoCoinsGanados,
                        totalReferidos = uiState.totalReferidos
                    )
                }
            }

            // Código de referido
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                ) {
                    CodigoReferidoCard(
                        codigo = uiState.codigoReferido,
                        onCopiar = {
                            clipboardManager.setText(AnnotatedString(uiState.codigoReferido))
                            showCopiedSnackbar = true
                        },
                        onCompartir = {
                            // TODO: Share intent
                        }
                    )
                }
            }

            // Cómo funciona
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically()
                ) {
                    ComoFuncionaCard()
                }
            }

            // Lista de referidos
            if (uiState.referidos.isNotEmpty()) {
                item {
                    Text(
                        text = "Tus Referidos (${uiState.totalReferidos})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                }

                items(uiState.referidos) { referido ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                    ) {
                        ReferidoCard(referido = referido)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Snackbar copiado
        if (showCopiedSnackbar) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showCopiedSnackbar = false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = EcoGreenPrimary
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Código copiado al portapapeles", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun RecompensaCard(
    ecoCoinsGanados: Int,
    totalReferidos: Int
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎁 Recompensa por Referidos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = null,
                            tint = EcoOrange,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "$ecoCoinsGanados",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "EcoCoins ganados",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalReferidos",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Amigos referidos",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Divider(color = Color.White.copy(alpha = 0.3f))

            Text(
                text = "¡Gana 50 EcoCoins por cada amigo!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CodigoReferidoCard(
    codigo: String,
    onCopiar: () -> Unit,
    onCompartir: () -> Unit
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
                text = "Tu Código de Referido",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            // Código grande
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = EcoGreenLight.copy(alpha = 0.1f)
            ) {
                Text(
                    text = codigo,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 20.dp),
                    letterSpacing = 4.sp
                )
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCopiar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreenPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copiar")
                }

                OutlinedButton(
                    onClick = onCompartir,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = EcoGreenPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartir")
                }
            }
        }
    }
}

@Composable
fun ComoFuncionaCard() {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "¿Cómo funciona?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            PasoReferido(
                numero = "1",
                titulo = "Comparte tu código",
                descripcion = "Envía tu código único a tus amigos"
            )

            PasoReferido(
                numero = "2",
                titulo = "Tu amigo se registra",
                descripcion = "Usa tu código al crear su cuenta"
            )

            PasoReferido(
                numero = "3",
                titulo = "¡Ambos ganan!",
                descripcion = "Tú ganas 50 EcoCoins y tu amigo 25 de bienvenida"
            )
        }
    }
}

@Composable
fun PasoReferido(
    numero: String,
    titulo: String,
    descripcion: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(EcoGreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = numero,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = descripcion,
                fontSize = 13.sp,
                color = Color(0xFF757575),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ReferidoCard(referido: ReferidoItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(EcoGreenPrimary, EcoGreenLight)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = referido.nombre.first().uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = referido.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "Unido ${referido.fechaRegistro}",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            // EcoCoins ganados
            Column(horizontalAlignment = Alignment.End) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = EcoOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+50",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                }
                Text(
                    text = "Ganados",
                    fontSize = 11.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

data class ReferidoItem(
    val id: String,
    val nombre: String,
    val fechaRegistro: String
)