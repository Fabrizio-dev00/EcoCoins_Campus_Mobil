package com.ecocoins.campus.presentation.ranking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.PeriodoRanking

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val GoldColor = Color(0xFFFFD700)
private val SilverColor = Color(0xFFC0C0C0)
private val BronzeColor = Color(0xFFCD7F32)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onNavigateBack: () -> Unit,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPeriodo by remember { mutableStateOf(PeriodoRanking.SEMANAL) }

    LaunchedEffect(selectedPeriodo) {
        viewModel.loadRanking(selectedPeriodo.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ranking 🏆",
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
                    IconButton(onClick = { viewModel.refresh(selectedPeriodo.id) }) {
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EcoGreenPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de periodo
                item {
                    PeriodoSelector(
                        periodos = PeriodoRanking.valores(),
                        selectedPeriodo = selectedPeriodo,
                        onPeriodoSelected = { selectedPeriodo = it }
                    )
                }

                // Mi posición
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                    ) {
                        MiPosicionCard(
                            posicion = uiState.miPosicion,
                            puntos = uiState.miPuntos,
                            totalUsuarios = uiState.totalUsuarios
                        )
                    }
                }

                // Podio (Top 3)
                item {
                    AnimatedVisibility(
                        visible = uiState.topUsuarios.isNotEmpty(),
                        enter = fadeIn() + expandVertically()
                    ) {
                        PodioCard(usuarios = uiState.topUsuarios.take(3))
                    }
                }

                // Título resto del ranking
                if (uiState.topUsuarios.size > 3) {
                    item {
                        Text(
                            text = "Resto del Ranking",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Lista del resto (desde posición 4)
                itemsIndexed(
                    items = uiState.topUsuarios.drop(3),
                    key = { _, usuario -> usuario.id }
                ) { index, usuario ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = index * 50
                            )
                        ) + fadeIn()
                    ) {
                        RankingUserCard(usuario = usuario)
                    }
                }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Error
        uiState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
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

@Composable
fun PeriodoSelector(
    periodos: List<PeriodoRanking>,
    selectedPeriodo: PeriodoRanking,
    onPeriodoSelected: (PeriodoRanking) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periodos.forEach { periodo ->
                val isSelected = periodo.id == selectedPeriodo.id

                Button(
                    onClick = { onPeriodoSelected(periodo) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) EcoGreenPrimary else Color(0xFFF5F5F5),
                        contentColor = if (isSelected) Color.White else Color(0xFF757575)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = periodo.nombre,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun MiPosicionCard(
    posicion: Int,
    puntos: Int,
    totalUsuarios: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Tu Posición",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#$posicion",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "de $totalUsuarios",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Tus Puntos",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = GoldColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "$puntos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PodioCard(usuarios: List<com.ecocoins.campus.data.model.RankingUsuario>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                text = "🏆 Top 3",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary
            )

            // Podio visual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Segundo lugar
                if (usuarios.size >= 2) {
                    PodioPosition(
                        usuario = usuarios[1],
                        posicion = 2,
                        height = 120.dp,
                        color = SilverColor
                    )
                }

                // Primer lugar
                if (usuarios.isNotEmpty()) {
                    PodioPosition(
                        usuario = usuarios[0],
                        posicion = 1,
                        height = 150.dp,
                        color = GoldColor
                    )
                }

                // Tercer lugar
                if (usuarios.size >= 3) {
                    PodioPosition(
                        usuario = usuarios[2],
                        posicion = 3,
                        height = 100.dp,
                        color = BronzeColor
                    )
                }
            }
        }
    }
}

@Composable
fun PodioPosition(
    usuario: com.ecocoins.campus.data.model.RankingUsuario,
    posicion: Int,
    height: Dp,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Corona para el primero
        if (posicion == 1) {
            val infiniteTransition = rememberInfiniteTransition(label = "crown")
            val rotation by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "rotation"
            )

            Text(
                text = "👑",
                fontSize = 24.sp,
                modifier = Modifier.rotate(rotation)
            )
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(color, color.copy(alpha = 0.7f))
                    )
                )
                .border(3.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getInitials(usuario.nombre),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Nombre
        Text(
            text = usuario.nombre.split(" ").first(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        // Pedestal
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(color.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "#$posicion",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${usuario.ecoCoins}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun RankingUserCard(usuario: com.ecocoins.campus.data.model.RankingUsuario) {
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
            // Posición
            Text(
                text = "#${usuario.posicion}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary,
                modifier = Modifier.width(40.dp)
            )

            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(EcoGreenLight.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getInitials(usuario.nombre),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = usuario.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Text(
                    text = usuario.carrera,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            // Puntos
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = GoldColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${usuario.ecoCoins}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }
                Text(
                    text = "${usuario.totalReciclajes} reciclajes",
                    fontSize = 11.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

fun getInitials(nombre: String): String {
    val parts = nombre.trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.isNotEmpty() -> parts[0].take(2).uppercase()
        else -> "?"
    }
}