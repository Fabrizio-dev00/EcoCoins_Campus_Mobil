package com.ecocoins.campus.presentation.educacion

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.ContenidoEducativo
import com.ecocoins.campus.data.model.Resource

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducacionScreen(
    onNavigateBack: () -> Unit,
    viewModel: EducacionViewModel = hiltViewModel()
) {
    // ✅ CAMBIO: Usar observeAsState() para LiveData
    val contenidosState by viewModel.contenidos.observeAsState()
    val progresoState by viewModel.progreso.observeAsState()

    var selectedContenido by remember { mutableStateOf<ContenidoEducativo?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarContenidos()
        viewModel.cargarProgreso()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Educación 📚",
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
                    IconButton(onClick = { viewModel.refresh() }) {
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
        // ✅ CAMBIO: Verificar loading desde Resource
        if (contenidosState is Resource.Loading && (contenidosState as? Resource.Success)?.data == null) {
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
                // Progreso de aprendizaje
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                    ) {
                        val progreso = (progresoState as? Resource.Success)?.data
                        val contenidos = (contenidosState as? Resource.Success)?.data ?: emptyList()

                        ProgresoAprendizajeCard(
                            contenidosCompletados = progreso?.contenidosCompletados ?: 0,
                            totalContenidos = contenidos.size,
                            ecoCoinsGanados = progreso?.ecoCoinsGanados ?: 0
                        )
                    }
                }

                // Contenidos
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Contenidos Disponibles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )

                        val contenidos = (contenidosState as? Resource.Success)?.data ?: emptyList()
                        Text(
                            text = "${contenidos.size} items",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                // ✅ CAMBIO: Obtener contenidos desde Resource
                val contenidos = (contenidosState as? Resource.Success)?.data ?: emptyList()

                if (contenidos.isEmpty()) {
                    item {
                        EmptyContenidosMessage()
                    }
                } else {
                    items(contenidos) { contenido ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                        ) {
                            ContenidoCard(
                                contenido = contenido,
                                onClick = { selectedContenido = it }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Diálogo de detalle
        selectedContenido?.let { contenido ->
            ContenidoDetailDialog(
                contenido = contenido,
                onDismiss = { selectedContenido = null },
                onCompletar = {
                    viewModel.completarContenido(it)
                    selectedContenido = null
                }
            )
        }
    }
}

@Composable
fun ProgresoAprendizajeCard(
    contenidosCompletados: Int,
    totalContenidos: Int,
    ecoCoinsGanados: Int
) {
    val progreso = if (totalContenidos > 0) {
        (contenidosCompletados.toFloat() / totalContenidos.toFloat())
    } else 0f

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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tu Progreso",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "$contenidosCompletados / $totalContenidos",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Contenidos completados",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📚",
                        fontSize = 32.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "${(progreso * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = EcoOrange,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EcoCoins ganados",
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
                        tint = EcoOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "+$ecoCoinsGanados",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ContenidoCard(
    contenido: ContenidoEducativo,
    onClick: (ContenidoEducativo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(contenido) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(EcoGreenPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = contenido.titulo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = contenido.categoria.toString(),
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            // Descripción
            Text(
                text = contenido.descripcion,
                fontSize = 14.sp,
                color = Color(0xFF757575),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider()

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Dificultad
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EcoGreenPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = contenido.dificultad.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Duración
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${contenido.duracionMinutos} min",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                // Recompensa
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
                        text = "+${contenido.recompensaEcoCoins}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                }
            }
        }
    }
}

@Composable
fun ContenidoDetailDialog(
    contenido: ContenidoEducativo,
    onDismiss: () -> Unit,
    onCompletar: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = contenido.titulo,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoGreenPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = contenido.dificultad.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = contenido.descripcion,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                item {
                    HorizontalDivider()
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Puntos Clave:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )

                        contenido.puntosClave.forEach { punto ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EcoGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = punto,
                                    fontSize = 13.sp,
                                    color = Color(0xFF212121),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider()
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF757575),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${contenido.duracionMinutos} minutos",
                                fontSize = 13.sp,
                                color = Color(0xFF757575)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Paid,
                                contentDescription = null,
                                tint = EcoOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "+${contenido.recompensaEcoCoins} EcoCoins",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoOrange
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCompletar(contenido.id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                )
            ) {
                Text("Marcar como Completado")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun EmptyContenidosMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📚",
                fontSize = 48.sp
            )
            Text(
                text = "No hay contenidos disponibles",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = "Pronto agregaremos más contenido educativo",
                fontSize = 14.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )
        }
    }
}