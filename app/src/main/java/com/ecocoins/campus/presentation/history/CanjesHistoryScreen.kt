package com.ecocoins.campus.presentation.history

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.Canje
import java.text.SimpleDateFormat
import java.util.*

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val StatusPending = Color(0xFFFFB74D)
private val StatusCompleted = Color(0xFF4CAF50)
private val StatusCancelled = Color(0xFFE53935)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanjesHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: CanjesHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedCanje by remember { mutableStateOf<Canje?>(null) }

    val tabs = listOf("Todos", "Pendientes", "Completados", "Cancelados")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mis Canjes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text(
                            text = "${uiState.canjes.size} canjes realizados",
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
                    // Resumen de gastos
                    ExpenseSummaryCard(
                        totalCanjes = uiState.canjes.size,
                        totalGastado = uiState.totalEcoCoinsGastados,
                        pendientes = uiState.canjes.count { it.estado == "PENDIENTE" },
                        modifier = Modifier.padding(16.dp)
                    )

                    // Tabs de filtro
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = EcoGreenPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de canjes filtrada
                    val filteredCanjes = when (tabs[selectedTab]) {
                        "Todos" -> uiState.canjes
                        "Pendientes" -> uiState.canjes.filter { it.estado == "PENDIENTE" }
                        "Completados" -> uiState.canjes.filter { it.estado == "COMPLETADO" }
                        "Cancelados" -> uiState.canjes.filter { it.estado == "CANCELADO" }
                        else -> uiState.canjes
                    }

                    if (filteredCanjes.isEmpty()) {
                        EmptyCanjesState(selectedTab = tabs[selectedTab])
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredCanjes,
                                key = { it.id }
                            ) { canje ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInHorizontally() + fadeIn(),
                                    exit = slideOutHorizontally() + fadeOut()
                                ) {
                                    CanjeCard(
                                        canje = canje,
                                        onClick = {
                                            selectedCanje = canje
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
    if (showDetailDialog && selectedCanje != null) {
        CanjeDetailDialog(
            canje = selectedCanje!!,
            onDismiss = {
                showDetailDialog = false
                selectedCanje = null
            }
        )
    }
}

@Composable
fun ExpenseSummaryCard(
    totalCanjes: Int,
    totalGastado: Int,
    pendientes: Int,
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
            SummaryItem(
                icon = Icons.Default.ShoppingBag,
                value = totalCanjes.toString(),
                label = "Total canjes",
                color = EcoGreenPrimary
            )

            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = Color(0xFFE0E0E0)
            )

            SummaryItem(
                icon = Icons.Default.Paid,
                value = totalGastado.toString(),
                label = "Gastados",
                color = EcoOrange
            )

            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = Color(0xFFE0E0E0)
            )

            SummaryItem(
                icon = Icons.Default.HourglassEmpty,
                value = pendientes.toString(),
                label = "Pendientes",
                color = StatusPending
            )
        }
    }
}

@Composable
fun SummaryItem(
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
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CanjeCard(
    canje: Canje,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono y nombre
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        getEstadoColor(canje.estado),
                                        getEstadoColor(canje.estado).copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = canje.recompensaNombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121),
                            maxLines = 2
                        )
                        Text(
                            text = "ID: ${canje.id.takeLast(8)}",
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }

                // Estado badge
                EstadoBadge(estado = canje.estado)
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF757575)
                        )
                        Text(
                            text = formatDate(canje.fechaCanje),
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }

                    canje.fechaEntrega?.let { fecha ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocalShipping,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF757575)
                            )
                            Text(
                                text = "Entrega: ${formatDate(fecha)}",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                }

                // Costo
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoOrange.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
                            text = "${canje.costoEcoCoins}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoBadge(estado: String) {
    val (color, text, icon) = when (estado) {
        "PENDIENTE" -> Triple(StatusPending, "Pendiente", Icons.Default.HourglassEmpty)
        "COMPLETADO" -> Triple(StatusCompleted, "Completado", Icons.Default.CheckCircle)
        "CANCELADO" -> Triple(StatusCancelled, "Cancelado", Icons.Default.Cancel)
        else -> Triple(Color.Gray, estado, Icons.Default.Help)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanjeDetailDialog(
    canje: Canje,
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
                        text = "Detalle del Canje",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                HorizontalDivider()

                // Estado destacado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    EstadoBadge(estado = canje.estado)
                }

                // ID del canje
                DetailRow(
                    icon = Icons.Default.Tag,
                    label = "ID del Canje",
                    value = canje.id
                )

                // Recompensa
                DetailRow(
                    icon = Icons.Default.CardGiftcard,
                    label = "Recompensa",
                    value = canje.recompensaNombre
                )

                // Costo
                DetailRow(
                    icon = Icons.Default.Paid,
                    label = "Costo",
                    value = "${canje.costoEcoCoins} EcoCoins",
                    valueColor = EcoOrange
                )

                // Usuario
                DetailRow(
                    icon = Icons.Default.Person,
                    label = "Canjeado por",
                    value = canje.usuarioNombre
                )

                // Fecha de canje
                DetailRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Fecha de canje",
                    value = formatDateLong(canje.fechaCanje)
                )

                // Fecha de entrega
                canje.fechaEntrega?.let { fecha ->
                    DetailRow(
                        icon = Icons.Default.LocalShipping,
                        label = "Fecha de entrega",
                        value = formatDateLong(fecha)
                    )
                }

                // Dirección de entrega
                canje.direccionEntrega?.let { direccion ->
                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "Dirección de entrega",
                        value = direccion
                    )
                }

                // Mensaje según estado
                when (canje.estado) {
                    "PENDIENTE" -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = StatusPending.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = StatusPending,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Tu canje está siendo procesado. Te notificaremos cuando esté listo.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF212121)
                                )
                            }
                        }
                    }
                    "COMPLETADO" -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = StatusCompleted.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusCompleted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "¡Canje completado! Tu recompensa fue entregada.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF212121)
                                )
                            }
                        }
                    }
                    "CANCELADO" -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = StatusCancelled.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = StatusCancelled,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Este canje fue cancelado. Tus EcoCoins fueron reembolsados.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF212121)
                                )
                            }
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
fun EmptyCanjesState(selectedTab: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ShoppingBag,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when (selectedTab) {
                "Pendientes" -> "Sin canjes pendientes"
                "Completados" -> "Sin canjes completados"
                "Cancelados" -> "Sin canjes cancelados"
                else -> "Sin canjes realizados"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (selectedTab) {
                "Pendientes" -> "Todos tus canjes están completos o cancelados"
                "Completados" -> "Aún no has completado ningún canje"
                "Cancelados" -> "No tienes canjes cancelados"
                else -> "Comienza a canjear recompensas para ver tu historial"
            },
            fontSize = 14.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )
    }
}

// Helper functions
fun getEstadoColor(estado: String): Color {
    return when (estado) {
        "PENDIENTE" -> StatusPending
        "COMPLETADO" -> StatusCompleted
        "CANCELADO" -> StatusCancelled
        else -> Color.Gray
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