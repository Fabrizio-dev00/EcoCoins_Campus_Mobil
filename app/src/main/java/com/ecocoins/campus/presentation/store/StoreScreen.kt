package com.ecocoins.campus.presentation.store

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.Profesor
import com.ecocoins.campus.data.model.RecompensaProfesor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Colores personalizados
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val EcoOrangeLight = Color(0xFFFFB74D)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var showProfesorDialog by remember { mutableStateOf(false) }
    var selectedProfesor by remember { mutableStateOf<Profesor?>(null) }

    val filters = listOf("Todos", "Puntos Extra", "Tardanzas", "Trabajos")

    Scaffold(
        topBar = {
            StoreTopBar(
                onNavigateBack = onNavigateBack,
                ecoCoins = uiState.userEcoCoins
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = EcoGreenPrimary)
                        Text(
                            "Cargando profesores...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (uiState.profesores.isEmpty()) {
                EmptyStoreState(modifier = Modifier.padding(padding))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Barra de búsqueda
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier.padding(16.dp)
                    )

                    // Filtros
                    FilterChips(
                        filters = filters,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid de profesores
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.profesores.filter { profesor ->
                                // Filtrar por búsqueda
                                val matchesSearch = profesor.getNombreCompleto()
                                    .contains(searchQuery, ignoreCase = true) ||
                                        profesor.especialidad.contains(searchQuery, ignoreCase = true)

                                // Filtrar por tipo de recompensa
                                val matchesFilter = when (selectedFilter) {
                                    "Todos" -> true
                                    "Puntos Extra" -> profesor.recompensas.any { it.tipo == "PUNTO_EXTRA" }
                                    "Tardanzas" -> profesor.recompensas.any { it.tipo == "QUITAR_TARDANZA" }
                                    "Trabajos" -> profesor.recompensas.any { it.tipo == "TRABAJO_ADICIONAL" }
                                    else -> true
                                }

                                matchesSearch && matchesFilter
                            },
                            key = { it.id }
                        ) { profesor ->
                            AnimatedVisibility(
                                visible = true,
                                enter = scaleIn() + fadeIn(),
                                exit = scaleOut() + fadeOut()
                            ) {
                                ProfesorCard(
                                    profesor = profesor,
                                    onClick = {
                                        selectedProfesor = profesor
                                        showProfesorDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Mensaje de error
            uiState.error?.let { error ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Snackbar(
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

            // Mensaje de éxito
            AnimatedVisibility(
                visible = uiState.canjeExitoso,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "¡Recompensa canjeada exitosamente!",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }

    // Diálogo de detalles del profesor
    if (showProfesorDialog && selectedProfesor != null) {
        ProfesorDetailDialog(
            profesor = selectedProfesor!!,
            userEcoCoins = uiState.userEcoCoins,
            onDismiss = {
                showProfesorDialog = false
                selectedProfesor = null
            },
            onCanjear = { recompensa ->
                viewModel.canjearRecompensa(selectedProfesor!!.id, recompensa.id)
                showProfesorDialog = false
                selectedProfesor = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreTopBar(
    onNavigateBack: () -> Unit,
    ecoCoins: Int
) {
    TopAppBar(
        title = {
            Text(
                text = "Tienda de Recompensas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        actions = {
            // Badge de EcoCoins
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = EcoOrangeLight.copy(alpha = 0.2f),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = "EcoCoins",
                        tint = EcoOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = ecoCoins.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar profesores o recompensas...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EcoGreenPrimary,
            unfocusedBorderColor = Color(0xFFE0E0E0)
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EcoGreenPrimary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ProfesorCard(
    profesor: Profesor,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press"
    )

    Card(
        onClick = {
            isPressed = true
            kotlinx.coroutines.GlobalScope.launch {
                delay(100)
                isPressed = false
                onClick()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatar del profesor
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(EcoGreenPrimary, EcoGreenLight)
                        )
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profesor.getIniciales(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Nombre y especialidad
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = profesor.getNombreCompleto(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = profesor.especialidad,
                    fontSize = 12.sp,
                    color = Color(0xFF757575),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Número de recompensas
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = EcoOrange
                        )
                        Text(
                            text = "${profesor.totalRecompensas}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoOrange
                        )
                    }
                    Text(
                        text = "recompensas",
                        fontSize = 10.sp,
                        color = Color(0xFF757575)
                    )
                }

                // Rating
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = EcoOrange
                        )
                        Text(
                            text = String.format("%.1f", profesor.rating),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                    }
                    Text(
                        text = "rating",
                        fontSize = 10.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesorDetailDialog(
    profesor: Profesor,
    userEcoCoins: Int,
    onDismiss: () -> Unit,
    onCanjear: (RecompensaProfesor) -> Unit
) {
    var selectedRecompensa by remember { mutableStateOf<RecompensaProfesor?>(null) }

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
                // Header con avatar y nombre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(EcoGreenPrimary, EcoGreenLight)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profesor.getIniciales(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profesor.getNombreCompleto(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = profesor.especialidad,
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = EcoOrange
                            )
                            Text(
                                text = String.format("%.1f", profesor.rating),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoOrange
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Divider()

                // Tu saldo
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EcoOrangeLight.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tu saldo:",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Paid,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = EcoOrange
                            )
                            Text(
                                text = "$userEcoCoins EcoCoins",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoOrange
                            )
                        }
                    }
                }

                // Lista de recompensas
                Text(
                    text = "Recompensas Disponibles:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    profesor.recompensas.forEach { recompensa ->
                        RecompensaItem(
                            recompensa = recompensa,
                            canAfford = userEcoCoins >= recompensa.costoEcoCoins,
                            isSelected = selectedRecompensa == recompensa,
                            onClick = { selectedRecompensa = recompensa }
                        )
                    }
                }

                // Botón de canje
                Button(
                    onClick = {
                        selectedRecompensa?.let { onCanjear(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = selectedRecompensa != null &&
                            userEcoCoins >= (selectedRecompensa?.costoEcoCoins ?: Int.MAX_VALUE),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreenPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedRecompensa != null) "Canjear" else "Selecciona una recompensa",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RecompensaItem(
    recompensa: RecompensaProfesor,
    canAfford: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EcoGreenLight.copy(alpha = 0.2f) else Color(0xFFF5F5F5)
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, EcoGreenPrimary)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recompensa.getEmojiTipo(),
                    fontSize = 24.sp
                )

                Column {
                    Text(
                        text = recompensa.getTipoFormateado(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = recompensa.descripcion,
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (canAfford) EcoOrange.copy(alpha = 0.2f) else Color(0xFFE0E0E0)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Paid,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (canAfford) EcoOrange else Color(0xFF9E9E9E)
                    )
                    Text(
                        text = "${recompensa.costoEcoCoins}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canAfford) EcoOrange else Color(0xFF9E9E9E)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStoreState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Store,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = EcoGreenPrimary.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No hay profesores disponibles",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Vuelve pronto para ver nuevas recompensas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}