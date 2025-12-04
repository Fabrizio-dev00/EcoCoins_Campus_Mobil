package com.ecocoins.campus.presentation.educacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducacionScreen(
    onNavigateToContenido: (String) -> Unit,
    onNavigateToQuiz: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EducacionViewModel = hiltViewModel()
) {
    val contenidos by viewModel.contenidos.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    // ⭐ Cargar datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadContenidos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Educación Ambiental") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(contenidos) { contenido ->
                    ContenidoCard(
                        contenido = contenido,
                        onClick = {
                            if (contenido.tipo == "QUIZ") {
                                onNavigateToQuiz(contenido.id)
                            } else {
                                onNavigateToContenido(contenido.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoCard(
    contenido: com.ecocoins.campus.data.model.ContenidoEducativo,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = contenido.titulo,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = contenido.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${contenido.duracionMinutos} min",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "+${contenido.recompensaEcoCoins} EC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
