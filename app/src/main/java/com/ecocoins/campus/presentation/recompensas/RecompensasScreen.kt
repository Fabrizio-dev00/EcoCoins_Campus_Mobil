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
import androidx.compose.ui.draw.clip
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
fun RecompensasScreen(
    onNavigateToDetail: (String) -> Unit,  // ⭐ Long -> String
    onNavigateToStore: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RecompensasViewModel = hiltViewModel()
) {
    val recompensas by viewModel.recompensas.observeAsState(emptyList())
    val ecoCoins by viewModel.ecoCoins.observeAsState(0L)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recompensas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, "Historial")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando recompensas...")
            }
            error != null -> {
                ErrorState(
                    message = error ?: "Error desconocido",
                    onRetry = { viewModel.loadRecompensas() }
                )
            }
            recompensas.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.CardGiftcard,
                    title = "Sin recompensas disponibles",
                    message = "No hay recompensas disponibles en este momento"
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Balance Card
                    BalanceCard(ecoCoins = ecoCoins)

                    // Recompensas Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recompensas) { recompensa ->
                            RecompensaCard(
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
}

@Composable
private fun BalanceCard(ecoCoins: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                    text = "Tu Saldo",
                    color = BackgroundWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$ecoCoins",
                        color = BackgroundWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "EcoCoins",
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
private fun RecompensaCard(
    recompensa: Recompensa,
    userEcoCoins: Long,
    onClick: () -> Unit
) {
    val canAfford = userEcoCoins >= recompensa.costoEcoCoins

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    .height(120.dp)
                    .background(
                        if (recompensa.imagenUrl != null)
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            EcoGreenLight.copy(alpha = 0.3f)
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
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Stock badge
                if (recompensa.stock <= 5 && recompensa.stock > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = StatusRejected
                    ) {
                        Text(
                            text = "¡Solo ${recompensa.stock}!",
                            color = BackgroundWhite,
                            fontSize = 10.sp,
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = recompensa.descripcion,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Precio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "EcoCoins",
                            tint = EcoOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recompensa.costoEcoCoins}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford) EcoGreenPrimary else StatusRejected
                        )
                    }

                    if (!canAfford) {
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
