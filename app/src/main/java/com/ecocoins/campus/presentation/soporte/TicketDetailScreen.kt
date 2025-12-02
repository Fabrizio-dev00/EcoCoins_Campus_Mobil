package com.ecocoins.campus.presentation.soporte

import androidx.compose.foundation.background
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
import com.ecocoins.campus.data.model.RespuestaTicket
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.CustomTextField
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: Long,
    onNavigateBack: () -> Unit,
    viewModel: SoporteViewModel = hiltViewModel()
) {
    val ticket by viewModel.selectedTicket.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    var nuevoMensaje by remember { mutableStateOf("") }

    LaunchedEffect(ticketId) {
        viewModel.getTicketById(ticketId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ticket #$ticketId") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    ticket?.let {
                        if (it.estado != "CERRADO") {
                            IconButton(
                                onClick = { viewModel.cerrarTicket(ticketId) }
                            ) {
                                Icon(Icons.Default.Close, "Cerrar ticket")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando ticket...")
            }
            ticket == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ticket no encontrado")
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header del ticket
                        item {
                            TicketHeaderCard(ticket!!)
                        }

                        // Descripción original
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Descripción",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = ticket!!.descripcion,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        // Respuestas
                        if (ticket!!.respuestas.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Conversación",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            items(ticket!!.respuestas) { respuesta ->
                                RespuestaCard(respuesta)
                            }
                        }
                    }

                    // Input para nueva respuesta
                    if (ticket!!.estado != "CERRADO") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                CustomTextField(
                                    value = nuevoMensaje,
                                    onValueChange = { nuevoMensaje = it },
                                    label = "Tu respuesta",
                                    placeholder = "Escribe tu mensaje...",
                                    minLines = 3
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                CustomButton(
                                    text = "Enviar",
                                    onClick = {
                                        if (nuevoMensaje.isNotBlank()) {
                                            viewModel.responderTicket(ticketId, nuevoMensaje)
                                            nuevoMensaje = ""
                                        }
                                    },
                                    enabled = nuevoMensaje.isNotBlank()
                                )
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Cerrado",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Este ticket está cerrado",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun TicketHeaderCard(ticket: com.ecocoins.campus.data.model.Ticket) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenLight.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = ticket.asunto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    InfoItem(label = "Estado", value = ticket.estado)
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(label = "Categoría", value = ticket.categoria)
                }
                Column(horizontalAlignment = Alignment.End) {
                    InfoItem(label = "Prioridad", value = ticket.prioridad)
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(label = "Creado", value = ticket.fechaCreacion.toFormattedDate())
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RespuestaCard(respuesta: RespuestaTicket) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (respuesta.esAdmin) Arrangement.Start else Arrangement.End
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (respuesta.esAdmin)
                    EcoGreenPrimary.copy(alpha = 0.1f)
                else
                    PlasticBlue.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (respuesta.esAdmin) EcoGreenPrimary else PlasticBlue,
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (respuesta.esAdmin) Icons.Default.Support else Icons.Default.Person,
                            contentDescription = "Usuario",
                            tint = BackgroundWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = respuesta.nombreUsuario,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = respuesta.fecha.toFormattedDate(),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = respuesta.mensaje,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}