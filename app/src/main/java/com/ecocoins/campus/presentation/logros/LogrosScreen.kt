package com.ecocoins.campus.presentation.logros

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.Logro
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogrosScreen(
    onNavigateBack: () -> Unit,
    viewModel: LogrosViewModel = hiltViewModel()
) {
    val logros by viewModel.logros.observeAsState(emptyList())
    val resumenLogros by viewModel.resumenLogros.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    var selectedCategory by remember { mutableStateOf("Todos") }
    val categorias = listOf("Todos", "Reciclaje", "EcoCoins", "Impacto", "Social")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Logros") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.verificarLogros() }) {
                        Icon(Icons.Default.Refresh, "Verificar")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando logros...")
            }
            error != null -> {
                ErrorState(
                    message = error ?: "Error desconocido",
                    onRetry = { viewModel.loadLogros() }
                )
            }
            logros.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.EmojiEvents,
                    title = "Sin logros",
                    message = "Empieza a reciclar para desbloquear logros"
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen de Logros
                    item {
                        resumenLogros?.let { resumen ->
                            ResumenLogrosCard(resumen)
                        }
                    }

                    // Filtros por categoría
                    item {
                        ScrollableTabRow(
                            selectedTabIndex = categorias.indexOf(selectedCategory),
                            edgePadding = 0.dp,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            categorias.forEach { categoria ->
                                Tab(
                                    selected = selectedCategory == categoria,
                                    onClick = { selectedCategory = categoria },
                                    text = { Text(categoria) }
                                )
                            }
                        }
                    }

                    // Lista de logros filtrados
                    val logrosFiltrados = if (selectedCategory == "Todos") {
                        logros
                    } else {
                        logros.filter { it.categoria == selectedCategory }
                    }

                    // Logros desbloqueados
                    val logrosDesbloqueados = logrosFiltrados.filter { it.desbloqueado }
                    if (logrosDesbloqueados.isNotEmpty()) {
                        item {
                            Text(
                                text = "Desbloqueados (${logrosDesbloqueados.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(logrosDesbloqueados) { logro ->
                            LogroCard(logro)
                        }
                    }

                    // Logros bloqueados
                    val logrosBloqueados = logrosFiltrados.filter { !it.desbloqueado }
                    if (logrosBloqueados.isNotEmpty()) {
                        item {
                            Text(
                                text = "Por desbloquear (${logrosBloqueados.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(logrosBloqueados) { logro ->
                            LogroCard(logro)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenLogrosCard(resumen: com.ecocoins.campus.data.model.LogrosResumen) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                    text = "Progreso Total",
                    color = BackgroundWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${resumen.logrosDesbloqueados}/${resumen.totalLogros}",
                    color = BackgroundWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%.0f", resumen.porcentajeCompletado)}% completado",
                    color = BackgroundWhite.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${resumen.ecoCoinsGanados}",
                    color = BackgroundWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "EcoCoins ganados",
                    color = BackgroundWhite.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun LogroCard(logro: Logro) {
    val rarezaColor = when (logro.rareza) {
        "COMUN" -> EcoGreenSecondary
        "RARO" -> PlasticBlue
        "EPICO" -> PaperBrown
        "LEGENDARIO" -> EcoOrange
        else -> EcoGreenSecondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (logro.desbloqueado)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (logro.desbloqueado) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del logro
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (logro.desbloqueado)
                            rarezaColor.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = logro.icono,
                    fontSize = 32.sp,
                    color = if (logro.desbloqueado)
                        rarezaColor
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del logro
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = logro.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (logro.desbloqueado)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge de rareza
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = rarezaColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = logro.rareza,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = rarezaColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = logro.descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Barra de progreso
                if (!logro.desbloqueado) {
                    Column {
                        LinearProgressIndicator(
                            progress = logro.porcentajeCompletado.toFloat() / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = rarezaColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${logro.progreso}/${logro.meta} • ${String.format("%.0f", logro.porcentajeCompletado)}%",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Recompensa
                if (logro.desbloqueado) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Desbloqueado",
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${logro.recompensaEcoCoins} EcoCoins obtenidos",
                            fontSize = 12.sp,
                            color = EcoGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Recompensa: ${logro.recompensaEcoCoins} EcoCoins",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}