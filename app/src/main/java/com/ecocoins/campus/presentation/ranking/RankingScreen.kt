package com.ecocoins.campus.presentation.ranking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel // ✅ Import de Hilt
import com.ecocoins.campus.data.model.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    viewModel: RankingViewModel = hiltViewModel() // ✅ Inyección con Hilt
) {
    val rankingState by viewModel.rankingList.observeAsState()
    val periodoSeleccionado by viewModel.periodoSeleccionado.observeAsState("SEMANAL")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Selector de periodo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = periodoSeleccionado == "SEMANAL",
                    onClick = { viewModel.cambiarPeriodo("SEMANAL") },
                    label = { Text("Semanal") }
                )
                FilterChip(
                    selected = periodoSeleccionado == "MENSUAL",
                    onClick = { viewModel.cambiarPeriodo("MENSUAL") },
                    label = { Text("Mensual") }
                )
                FilterChip(
                    selected = periodoSeleccionado == "HISTORICO",
                    onClick = { viewModel.cambiarPeriodo("HISTORICO") },
                    label = { Text("Histórico") }
                )
            }

            // Contenido
            when (rankingState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val ranking = (rankingState as Resource.Success).data ?: emptyList()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ranking) { item ->
                            RankingItemCard(item)
                        }
                    }
                }
                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (rankingState as Resource.Error).message ?: "Error",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun RankingItemCard(item: com.ecocoins.campus.data.model.RankingItem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Posición
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = when (item.posicion) {
                        1 -> MaterialTheme.colorScheme.primaryContainer
                        2 -> MaterialTheme.colorScheme.secondaryContainer
                        3 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = "#${item.posicion}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Nombre y stats
                Column {
                    Text(
                        text = item.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${item.kgReciclados} kg • ${item.totalReciclajes} reciclajes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // EcoCoins
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.ecoCoinsGanados}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "EcoCoins",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}