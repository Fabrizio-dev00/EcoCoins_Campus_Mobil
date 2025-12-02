package com.ecocoins.campus.presentation.notificaciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    viewModel: NotificacionesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val notificaciones by viewModel.notificaciones.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotificaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.marcarTodasComoLeidas() }) {
                        Icon(Icons.Default.DoneAll, "Marcar todas como leídas")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Error desconocido",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                notificaciones.isEmpty() -> {
                    Text(
                        text = "No hay notificaciones",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notificaciones) { notificacion ->
                            // Card de notificación (implementar según necesites)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = notificacion.titulo)
                                    Text(text = notificacion.mensaje)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}