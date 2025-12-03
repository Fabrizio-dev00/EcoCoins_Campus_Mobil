package com.ecocoins.campus.presentation.educacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.components.SuccessDialog
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoDetailScreen(
    contenidoId: String,  // ⭐ Long -> String
    onNavigateBack: () -> Unit,
    viewModel: EducacionViewModel = hiltViewModel()
) {
    val contenido by viewModel.selectedContenido.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(contenidoId) {
        viewModel.getContenidoById(contenidoId)
    }

    SuccessDialog(
        showDialog = showSuccessDialog,
        title = "¡Contenido Completado!",
        message = "Has ganado ${contenido?.recompensaEcoCoins} EcoCoins",
        onDismiss = {
            showSuccessDialog = false
            onNavigateBack()
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contenido Educativo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando contenido...")
            }
            contenido == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Contenido no encontrado")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Imagen
                    item {
                        if (contenido!!.imagenUrl != null) {
                            AsyncImage(
                                model = contenido!!.imagenUrl,
                                contentDescription = contenido!!.titulo,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }

                    // Título y descripción
                    item {
                        Column {
                            Text(
                                text = contenido!!.titulo,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Chip(text = contenido!!.categoria)
                                Chip(text = contenido!!.dificultad)
                                Chip(text = "${contenido!!.duracionMinutos} min")
                            }
                        }
                    }

                    item {
                        Text(
                            text = contenido!!.descripcion,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }

                    // Puntos clave
                    item {
                        Text(
                            text = "Puntos Clave",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(contenido!!.puntosClave) { punto ->
                        PuntoClave(punto)
                    }

                    // Botón completar
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomButton(
                            text = "Marcar como Completado (+${contenido!!.recompensaEcoCoins} EC)",
                            onClick = {
                                viewModel.completarContenido(contenidoId)
                                showSuccessDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = EcoGreenPrimary.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = EcoGreenPrimary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PuntoClave(punto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Punto",
                tint = EcoGreenPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = punto,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
