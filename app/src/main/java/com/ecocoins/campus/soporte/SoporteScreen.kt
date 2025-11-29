package com.ecocoins.campus.presentation.soporte

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.*

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoporteScreen(
    onNavigateBack: () -> Unit,
    viewModel: SoporteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showNuevoTicketDialog by remember { mutableStateOf(false) }
    var selectedTicket by remember { mutableStateOf<Ticket?>(null) }
    var selectedFAQ by remember { mutableStateOf<FAQ?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Soporte 🎧",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showNuevoTicketDialog = true },
                    containerColor = EcoGreenPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Ticket")
                }
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = EcoGreenPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EcoGreenPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Preguntas Frecuentes",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Mis Tickets",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            // Content
            when (selectedTab) {
                0 -> FAQContent(
                    faqs = uiState.faqs,
                    isLoading = uiState.isLoading,
                    onFAQClick = { selectedFAQ = it }
                )
                1 -> TicketsContent(
                    tickets = uiState.tickets,
                    isLoading = uiState.isLoading,
                    onTicketClick = { selectedTicket = it }
                )
            }
        }

        // Dialogs
        if (showNuevoTicketDialog) {
            NuevoTicketDialog(
                onDismiss = { showNuevoTicketDialog = false },
                onCreate = { asunto, descripcion, categoria, prioridad ->
                    viewModel.crearTicket(asunto, descripcion, categoria, prioridad)
                    showNuevoTicketDialog = false
                }
            )
        }

        selectedTicket?.let { ticket ->
            TicketDetailDialog(
                ticket = ticket,
                onDismiss = { selectedTicket = null },
                onEnviarRespuesta = { mensaje ->
                    viewModel.responderTicket(ticket.id, mensaje)
                }
            )
        }

        selectedFAQ?.let { faq ->
            FAQDetailDialog(
                faq = faq,
                onDismiss = { selectedFAQ = null },
                onMarcarUtil = { viewModel.marcarFAQUtil(it) }
            )
        }
    }
}

@Composable
fun FAQContent(
    faqs: List<FAQ>,
    isLoading: Boolean,
    onFAQClick: (FAQ) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = EcoGreenPrimary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Encuentra respuestas rápidas",
                    fontSize = 16.sp,
                    color = Color(0xFF757575)
                )
            }

            if (faqs.isEmpty()) {
                item {
                    EmptyFAQsCard()
                }
            } else {
                items(faqs) { faq ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                    ) {
                        FAQCard(
                            faq = faq,
                            onClick = { onFAQClick(it) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TicketsContent(
    tickets: List<Ticket>,
    isLoading: Boolean,
    onTicketClick: (Ticket) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = EcoGreenPrimary)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tus consultas (${tickets.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Text(
                        text = "${tickets.count { it.estado == EstadoTicket.ABIERTO }} abiertos",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }
            }

            if (tickets.isEmpty()) {
                item {
                    EmptyTicketsCard()
                }
            } else {
                items(tickets) { ticket ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                    ) {
                        TicketCard(
                            ticket = ticket,
                            onClick = { onTicketClick(it) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun FAQCard(
    faq: FAQ,
    onClick: (FAQ) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(faq) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EcoGreenLight.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = faq.pregunta,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = getCategoriaFAQNombre(faq.categoria),
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9E9E9E)
            )
        }
    }
}

@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: (Ticket) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(ticket) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ticket.asunto,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Ticket #${ticket.id}",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getEstadoTicketColor(ticket.estado).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = getEstadoTicketNombre(ticket.estado),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = getEstadoTicketColor(ticket.estado),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Descripción
            Text(
                text = ticket.descripcion,
                fontSize = 14.sp,
                color = Color(0xFF757575),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Divider()

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Categoría
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getCategoriaTicketIcon(ticket.categoria),
                            contentDescription = null,
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = getCategoriaTicketNombre(ticket.categoria),
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }

                    // Prioridad
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getPrioridadColor(ticket.prioridad).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = getPrioridadNombre(ticket.prioridad),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = getPrioridadColor(ticket.prioridad),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Respuestas
                if (ticket.respuestas.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${ticket.respuestas.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoTicketDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, CategoriaTicket, PrioridadTicket) -> Unit
) {
    var asunto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CategoriaTicket.CONSULTA_ECOCOINS) }
    var prioridad by remember { mutableStateOf(PrioridadTicket.MEDIA) }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nuevo Ticket de Soporte",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = asunto,
                        onValueChange = { asunto = it },
                        label = { Text("Asunto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción del problema") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = it }
                    ) {
                        OutlinedTextField(
                            value = getCategoriaTicketNombre(categoria),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedCategoria) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false }
                        ) {
                            CategoriaTicket.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(getCategoriaTicketNombre(cat)) },
                                    onClick = {
                                        categoria = cat
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedPrioridad,
                        onExpandedChange = { expandedPrioridad = it }
                    ) {
                        OutlinedTextField(
                            value = getPrioridadNombre(prioridad),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Prioridad") },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedPrioridad) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedPrioridad,
                            onDismissRequest = { expandedPrioridad = false }
                        ) {
                            PrioridadTicket.values().forEach { pri ->
                                DropdownMenuItem(
                                    text = { Text(getPrioridadNombre(pri)) },
                                    onClick = {
                                        prioridad = pri
                                        expandedPrioridad = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (asunto.isNotBlank() && descripcion.isNotBlank()) {
                        onCreate(asunto, descripcion, categoria, prioridad)
                    }
                },
                enabled = asunto.isNotBlank() && descripcion.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                )
            ) {
                Text("Crear Ticket")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun TicketDetailDialog(
    ticket: Ticket,
    onDismiss: () -> Unit,
    onEnviarRespuesta: (String) -> Unit
) {
    var mensaje by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = ticket.asunto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getEstadoTicketColor(ticket.estado).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = getEstadoTicketNombre(ticket.estado),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = getEstadoTicketColor(ticket.estado),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Ticket #${ticket.id}",
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = ticket.descripcion,
                        fontSize = 14.sp,
                        color = Color(0xFF212121)
                    )
                }

                if (ticket.respuestas.isNotEmpty()) {
                    item {
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Respuestas:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                    }

                    items(ticket.respuestas) { respuesta ->
                        RespuestaItem(respuesta)
                    }
                }

                if (ticket.estado != EstadoTicket.CERRADO) {
                    item {
                        Divider()
                    }

                    item {
                        OutlinedTextField(
                            value = mensaje,
                            onValueChange = { mensaje = it },
                            label = { Text("Tu respuesta") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 4
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (ticket.estado != EstadoTicket.CERRADO && mensaje.isNotBlank()) {
                Button(
                    onClick = {
                        onEnviarRespuesta(mensaje)
                        mensaje = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreenPrimary
                    )
                ) {
                    Text("Enviar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun RespuestaItem(respuesta: RespuestaTicket) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (respuesta.esAdmin) EcoOrange.copy(alpha = 0.2f)
                    else EcoGreenLight.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (respuesta.esAdmin) Icons.Default.SupportAgent else Icons.Default.Person,
                contentDescription = null,
                tint = if (respuesta.esAdmin) EcoOrange else EcoGreenPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = respuesta.nombreUsuario,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = respuesta.mensaje,
                fontSize = 13.sp,
                color = Color(0xFF212121)
            )
            Text(
                text = respuesta.fecha,
                fontSize = 11.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun FAQDetailDialog(
    faq: FAQ,
    onDismiss: () -> Unit,
    onMarcarUtil: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = faq.pregunta,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = faq.respuesta,
                    fontSize = 14.sp,
                    color = Color(0xFF212121)
                )

                Divider()

                Text(
                    text = "¿Te fue útil esta respuesta?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF757575)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onMarcarUtil(faq.id)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                )
            ) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sí, útil")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun EmptyFAQsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "❓", fontSize = 48.sp)
            Text(
                text = "No hay preguntas frecuentes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
        }
    }
}

@Composable
fun EmptyTicketsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🎧", fontSize = 48.sp)
            Text(
                text = "No tienes tickets abiertos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = "Crea uno si necesitas ayuda",
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

// Helper functions
fun getCategoriaTicketNombre(categoria: CategoriaTicket): String {
    return when (categoria) {
        CategoriaTicket.PROBLEMA_TECNICO -> "Problema Técnico"
        CategoriaTicket.CONSULTA_ECOCOINS -> "Consulta EcoCoins"
        CategoriaTicket.PROBLEMA_CANJE -> "Problema con Canje"
        CategoriaTicket.PROBLEMA_RECICLAJE -> "Problema con Reciclaje"
        CategoriaTicket.SUGERENCIA -> "Sugerencia"
        CategoriaTicket.OTRO -> "Otro"
    }
}

fun getCategoriaTicketIcon(categoria: CategoriaTicket): ImageVector {
    return when (categoria) {
        CategoriaTicket.PROBLEMA_TECNICO -> Icons.Default.BugReport
        CategoriaTicket.CONSULTA_ECOCOINS -> Icons.Default.Paid
        CategoriaTicket.PROBLEMA_CANJE -> Icons.Default.Redeem
        CategoriaTicket.PROBLEMA_RECICLAJE -> Icons.Default.Recycling
        CategoriaTicket.SUGERENCIA -> Icons.Default.Lightbulb
        CategoriaTicket.OTRO -> Icons.Default.HelpOutline
    }
}

fun getPrioridadNombre(prioridad: PrioridadTicket): String {
    return when (prioridad) {
        PrioridadTicket.BAJA -> "Baja"
        PrioridadTicket.MEDIA -> "Media"
        PrioridadTicket.ALTA -> "Alta"
        PrioridadTicket.URGENTE -> "Urgente"
    }
}

fun getPrioridadColor(prioridad: PrioridadTicket): Color {
    return when (prioridad) {
        PrioridadTicket.BAJA -> Color(0xFF9E9E9E)
        PrioridadTicket.MEDIA -> Color(0xFF2196F3)
        PrioridadTicket.ALTA -> Color(0xFFFF9800)
        PrioridadTicket.URGENTE -> Color(0xFFE53935)
    }
}

fun getEstadoTicketNombre(estado: EstadoTicket): String {
    return when (estado) {
        EstadoTicket.ABIERTO -> "ABIERTO"
        EstadoTicket.EN_PROCESO -> "EN PROCESO"
        EstadoTicket.RESUELTO -> "RESUELTO"
        EstadoTicket.CERRADO -> "CERRADO"
    }
}

fun getEstadoTicketColor(estado: EstadoTicket): Color {
    return when (estado) {
        EstadoTicket.ABIERTO -> Color(0xFF2196F3)
        EstadoTicket.EN_PROCESO -> Color(0xFFFF9800)
        EstadoTicket.RESUELTO -> Color(0xFF4CAF50)
        EstadoTicket.CERRADO -> Color(0xFF9E9E9E)
    }
}

fun getCategoriaFAQNombre(categoria: CategoriaFAQ): String {
    return when (categoria) {
        CategoriaFAQ.CUENTA -> "Cuenta"
        CategoriaFAQ.RECICLAJE -> "Reciclaje"
        CategoriaFAQ.ECOCOINS -> "EcoCoins"
        CategoriaFAQ.CANJES -> "Canjes"
        CategoriaFAQ.GENERAL -> "General"
    }
}