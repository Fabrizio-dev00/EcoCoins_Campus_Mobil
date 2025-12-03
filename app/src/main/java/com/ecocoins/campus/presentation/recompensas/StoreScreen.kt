package com.ecocoins.campus.presentation.recompensas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    onNavigateToDetail: (String) -> Unit,  // ⭐ Long -> String
    onNavigateBack: () -> Unit,
    viewModel: RecompensasViewModel = hiltViewModel()
) {
    val recompensas by viewModel.recompensas.observeAsState(emptyList())
    val ecoCoins by viewModel.ecoCoins.observeAsState(0L)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    var selectedCategory by remember { mutableStateOf("Todas") }
    var sortBy by remember { mutableStateOf("Destacadas") }
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda de Recompensas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, "Filtros")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando tienda...")
            }
            error != null -> {
                ErrorState(
                    message = error ?: "Error desconocido",
                    onRetry = { viewModel.loadRecompensas() }
                )
            }
            recompensas.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.Store,
                    title = "Tienda vacía",
                    message = "No hay recompensas disponibles"
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Balance Card
                    BalanceHeaderCard(ecoCoins = ecoCoins)

                    // Categorías
                    val categorias = listOf("Todas") + recompensas.map { it.categoria }.distinct()
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

                    // Grid de recompensas
                    val recompensasFiltradas = if (selectedCategory == "Todas") {
                        recompensas
                    } else {
                        recompensas.filter { it.categoria == selectedCategory }
                    }.let { lista ->
                        when (sortBy) {
                            "Precio: Menor a Mayor" -> lista.sortedBy { it.costoEcoCoins }
                            "Precio: Mayor a Menor" -> lista.sortedByDescending { it.costoEcoCoins }
                            "Nombre A-Z" -> lista.sortedBy { it.nombre }
                            else -> lista
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recompensasFiltradas) { recompensa ->
                            StoreRecompensaCard(
                                recompensa = recompensa,
                                userEcoCoins = ecoCoins,
                                onClick = { onNavigateToDetail(recompensa.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de filtros
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Ordenar por") },
            text = {
                Column {
                    listOf(
                        "Destacadas",
                        "Precio: Menor a Mayor",
                        "Precio: Mayor a Menor",
                        "Nombre A-Z"
                    ).forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    sortBy = option
                                    showFilterDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = sortBy == option,
                                onClick = {
                                    sortBy = option
                                    showFilterDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun BalanceHeaderCard(ecoCoins: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
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
                    text = "Tu Saldo",
                    color = BackgroundWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$ecoCoins",
                        color = BackgroundWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "EC",
                        color = BackgroundWhite.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Saldo",
                tint = BackgroundWhite,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun StoreRecompensaCard(
    recompensa: Recompensa,
    userEcoCoins: Long,
    onClick: () -> Unit
) {
    val canAfford = userEcoCoins >= recompensa.costoEcoCoins
    val hasStock = recompensa.stock > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasStock) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        if (recompensa.imagenUrl != null)
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            EcoOrangeLight.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (recompensa.imagenUrl != null) {
                    AsyncImage(
                        model = recompensa.imagenUrl,
                        contentDescription = recompensa.nombre,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = recompensa.nombre,
                        tint = EcoOrange,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Badges
                if (!hasStock) {
                    Surface(
                        modifier = Modifier.align(Alignment.Center),
                        shape = RoundedCornerShape(8.dp),
                        color = StatusRejected
                    ) {
                        Text(
                            text = "AGOTADO",
                            color = BackgroundWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (recompensa.stock <= 5) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = StatusRejected
                    ) {
                        Text(
                            text = "¡${recompensa.stock}!",
                            color = BackgroundWhite,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Información
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = recompensa.nombre,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Precio",
                            tint = EcoOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recompensa.costoEcoCoins}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford && hasStock) EcoOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (hasStock && !canAfford) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
