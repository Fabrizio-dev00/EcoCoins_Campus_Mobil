package com.ecocoins.campus.presentation.history

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.ecocoins.campus.data.model.Reciclaje
import java.text.SimpleDateFormat
import java.util.*

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciclajesHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReciclajesHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Todos") }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedReciclaje by remember { mutableStateOf<Reciclaje?>(null) }

    val filters = listOf("Todos", "Plástico", "Papel", "Vidrio", "Metal")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Historial de Reciclajes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text(
                            text = "${uiState.reciclajes.size} reciclajes realizados",
                            fontSize = 13.sp,
                            color = Color(0xFF757575)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de estadísticas
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = "Estadísticas",
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
        Box(modifier = Modifier.fillMaxSize()) {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Estadísticas resumidas
                    StatsCard(
                        totalReciclajes = uiState.reciclajes.size,
                        totalKg = uiState.totalKgReciclados,
                        totalEcoCoins = uiState.totalEcoCoins,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Filtros
                    FilterChipsRow(
                        filters = filters,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de reciclajes
                    if (uiState.reciclajes.isEmpty()) {
                        EmptyHistoryState()
                    } else {
                        val filteredReciclajes = if (selectedFilter == "Todos") {
                            uiState.reciclajes
                        } else {
                            uiState.reciclajes.filter {
                                it.tipoMaterial.contains(selectedFilter, ignoreCase = true)
                            }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredReciclajes,
                                key = { it.id }
                            ) { reciclaje ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInHorizontally() + fadeIn(),
                                    exit = slideOutHorizontally() + fadeOut()
                                ) {
                                    ReciclajeHistoryCard(
                                        reciclaje = reciclaje,
                                        onClick = {
                                            selectedReciclaje = reciclaje
                                            showDetailDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Error message
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

    // Diálogo de detalles
    if (showDetailDialog && selectedReciclaje != null) {
        ReciclajeDetailDialog(
            reciclaje = selectedReciclaje!!,
            onDismiss = {
                showDetailDialog = false
                selectedReciclaje = null
            }
        )
    }
}

@Composable
fun StatsCard(
    totalReciclajes: Int,
    totalKg: Double,
    totalEcoCoins: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Recycling,
                value = totalReciclajes.toString(),
                label = "Reciclajes",
                color = EcoGreenPrimary
            )

            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = Color(0xFFE0E0E0)
            )

            StatItem(
                icon = Icons.Default.Scale,
                value = String.format("%.1f", totalKg),
                label = "Kg totales",
                color = EcoOrange
            )

            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = Color(0xFFE0E0E0)
            )

            StatItem(
                icon = Icons.Default.Paid,
                value = totalEcoCoins.toString(),
                label = "EcoCoins",
                color = EcoOrange
            )
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF757575)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipsRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EcoGreenPrimary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ReciclajeHistoryCard(
    reciclaje: Reciclaje,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ícono del material
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                getMaterialColor(reciclaje.tipoMaterial),
                                getMaterialColor(reciclaje.tipoMaterial).copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Recycling,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Información
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reciclaje.tipoMaterial,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    if (reciclaje.verificado) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EcoGreenPrimary.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = EcoGreenPrimary
                                )
                                Text(
                                    text = "Verificado",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Scale,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF757575)
                        )
                        Text(
                            text = String.format("%.2f kg", reciclaje.pesoKg),
                            fontSize = 13.sp,
                            color = Color(0xFF757575)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Paid,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = EcoOrange
                        )
                        Text(
                            text = "+${reciclaje.ecoCoinsGanadas}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoOrange
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF9E9E9E)
                    )
                    Text(
                        text = formatDate(reciclaje.fecha),
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            // Flecha
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9E9E9E)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciclajeDetailDialog(
    reciclaje: Reciclaje,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalle del Reciclaje",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                HorizontalDivider()

                // Imagen si existe
                reciclaje.fotoUrl?.let { url ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = "Foto del material",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Información detallada
                DetailRow(
                    icon = Icons.Default.Category,
                    label = "Material",
                    value = reciclaje.tipoMaterial
                )

                DetailRow(
                    icon = Icons.Default.Scale,
                    label = "Peso",
                    value = String.format("%.2f kg", reciclaje.pesoKg)
                )

                DetailRow(
                    icon = Icons.Default.Paid,
                    label = "EcoCoins ganados",
                    value = "+${reciclaje.ecoCoinsGanadas}",
                    valueColor = EcoOrange
                )

                DetailRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Fecha",
                    value = formatDateLong(reciclaje.fecha)
                )

                reciclaje.contenedorCodigo?.let { codigo ->
                    DetailRow(
                        icon = Icons.Default.QrCode,
                        label = "Código QR",
                        value = codigo
                    )
                }

                reciclaje.puntoRecoleccion?.let { punto ->
                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "Punto de recolección",
                        value = punto
                    )
                }

                if (reciclaje.verificado) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = EcoGreenPrimary.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Material verificado por IA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF212121)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(20.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
        }
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sin reciclajes aún",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Comienza a reciclar para ver tu historial aquí",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )
    }
}

// Helper functions
fun getMaterialColor(material: String): Color {
    return when (material.lowercase()) {
        "plástico", "plastico" -> Color(0xFF2196F3)
        "papel" -> Color(0xFF795548)
        "vidrio" -> Color(0xFF4CAF50)
        "metal" -> Color(0xFF607D8B)
        else -> Color(0xFF2D7A3E)
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString.take(10)
    }
}

fun formatDateLong(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd 'de' MMMM, yyyy 'a las' HH:mm", Locale("es"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}