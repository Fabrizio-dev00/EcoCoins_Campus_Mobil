package com.ecocoins.campus.presentation.educacion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.CategoriaEducativa
import com.ecocoins.campus.data.model.ContenidoEducativo
import com.ecocoins.campus.data.model.NivelDificultad
import com.ecocoins.campus.data.model.TipoContenido

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
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategoria by remember { mutableStateOf<CategoriaEducativa?>(null) }
    var selectedContenido by remember { mutableStateOf<ContenidoEducativo?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadContenidos()
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
                // Progreso de aprendizaje
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                    ) {
                        ProgresoAprendizajeCard(
                            contenidosCompletados = uiState.contenidosCompletados,
                            totalContenidos = uiState.contenidos.size,
                            ecoCoinsGanados = uiState.ecoCoinsGanados
                        )
                    }
                }

                // Categorías
                item {
                    Text(
                        text = "Categorías",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                }

                item {
                    CategoriasRow(
                        selectedCategoria = selectedCategoria,
                        onCategoriaSelected = { selectedCategoria = it }
                    )
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

                        Text(
                            text = "${uiState.contenidos.size} items",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                val contenidosFiltrados = if (selectedCategoria != null) {
                    uiState.contenidos.filter { it.categoria == selectedCategoria }
                } else {
                    uiState.contenidos
                }

                if (contenidosFiltrados.isEmpty()) {
                    item {
                        EmptyContenidosMessage()
                    }
                } else {
                    items(contenidosFiltrados) { contenido ->
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

            Divider(color = Color.White.copy(alpha = 0.3f))

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
fun CategoriasRow(
    selectedCategoria: CategoriaEducativa?,
    onCategoriaSelected: (CategoriaEducativa?) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Todos
        item {
            CategoriaChip(
                titulo = "Todos",
                icono = "📖",
                isSelected = selectedCategoria == null,
                onClick = { onCategoriaSelected(null) }
            )
        }

        // Categorías
        items(CategoriaEducativa.values().toList()) { categoria ->
            CategoriaChip(
                titulo = getCategoriaNombre(categoria),
                icono = getCategoriaIcono(categoria),
                isSelected = selectedCategoria == categoria,
                onClick = { onCategoriaSelected(categoria) }
            )
        }
    }
}

@Composable
fun CategoriaChip(
    titulo: String,
    icono: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EcoGreenPrimary else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icono,
                fontSize = 24.sp
            )
            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF212121)
            )
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
                            .background(getTipoContenidoColor(contenido.tipo).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getTipoContenidoIcon(contenido.tipo),
                            contentDescription = null,
                            tint = getTipoContenidoColor(contenido.tipo),
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
                            text = getCategoriaNombre(contenido.categoria),
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                if (contenido.completado) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completado",
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
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

            Divider()

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
                        color = getDificultadColor(contenido.dificultad).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = getDificultadNombre(contenido.dificultad),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = getDificultadColor(contenido.dificultad),
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
                        imageVector = getTipoContenidoIcon(contenido.tipo),
                        contentDescription = null,
                        tint = getTipoContenidoColor(contenido.tipo),
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
                    color = getDificultadColor(contenido.dificultad).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = getDificultadNombre(contenido.dificultad),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = getDificultadColor(contenido.dificultad),
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
                    Divider()
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
                    Divider()
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
            if (!contenido.completado) {
                Button(
                    onClick = { onCompletar(contenido.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreenPrimary
                    )
                ) {
                    Text("Marcar como Completado")
                }
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Helper functions
fun getCategoriaNombre(categoria: CategoriaEducativa): String {
    return when (categoria) {
        CategoriaEducativa.RECICLAJE_BASICO -> "Reciclaje Básico"
        CategoriaEducativa.SEPARACION_RESIDUOS -> "Separación de Residuos"
        CategoriaEducativa.IMPACTO_AMBIENTAL -> "Impacto Ambiental"
        CategoriaEducativa.ECONOMIA_CIRCULAR -> "Economía Circular"
        CategoriaEducativa.CONSEJOS_PRACTICOS -> "Consejos Prácticos"
    }
}

fun getCategoriaIcono(categoria: CategoriaEducativa): String {
    return when (categoria) {
        CategoriaEducativa.RECICLAJE_BASICO -> "♻️"
        CategoriaEducativa.SEPARACION_RESIDUOS -> "🗑️"
        CategoriaEducativa.IMPACTO_AMBIENTAL -> "🌍"
        CategoriaEducativa.ECONOMIA_CIRCULAR -> "🔄"
        CategoriaEducativa.CONSEJOS_PRACTICOS -> "💡"
    }
}

fun getTipoContenidoIcon(tipo: TipoContenido): ImageVector {
    return when (tipo) {
        TipoContenido.ARTICULO -> Icons.Default.Article
        TipoContenido.VIDEO -> Icons.Default.PlayCircle
        TipoContenido.INFOGRAFIA -> Icons.Default.Image
        TipoContenido.QUIZ -> Icons.Default.Quiz
        TipoContenido.GUIA -> Icons.Default.MenuBook
    }
}

fun getTipoContenidoColor(tipo: TipoContenido): Color {
    return when (tipo) {
        TipoContenido.ARTICULO -> Color(0xFF2196F3)
        TipoContenido.VIDEO -> Color(0xFFE91E63)
        TipoContenido.INFOGRAFIA -> Color(0xFF9C27B0)
        TipoContenido.QUIZ -> Color(0xFFFF9800)
        TipoContenido.GUIA -> Color(0xFF4CAF50)
    }
}

fun getDificultadColor(dificultad: NivelDificultad): Color {
    return when (dificultad) {
        NivelDificultad.PRINCIPIANTE -> Color(0xFF4CAF50)
        NivelDificultad.INTERMEDIO -> Color(0xFFFF9800)
        NivelDificultad.AVANZADO -> Color(0xFFE53935)
    }
}

fun getDificultadNombre(dificultad: NivelDificultad): String {
    return when (dificultad) {
        NivelDificultad.PRINCIPIANTE -> "PRINCIPIANTE"
        NivelDificultad.INTERMEDIO -> "INTERMEDIO"
        NivelDificultad.AVANZADO -> "AVANZADO"
    }
}