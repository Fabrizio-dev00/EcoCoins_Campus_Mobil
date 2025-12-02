package com.ecocoins.campus.presentation.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onNavigateToEdit: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToLogros: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val user by viewModel.user.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Configuración")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando perfil...")
            }
            user == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error al cargar perfil")
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
                    // Header del perfil
                    item {
                        PerfilHeader(
                            nombre = user!!.nombre,
                            email = user!!.email,
                            nivel = user!!.nivel,
                            carrera = user!!.carrera ?: "Sin carrera",
                            onEditClick = onNavigateToEdit
                        )
                    }

                    // EcoCoins Balance
                    item {
                        EcoCoinsCard(
                            ecoCoins = user!!.ecoCoins,
                            nivel = user!!.nivel
                        )
                    }

                    // Opciones del perfil
                    item {
                        Text(
                            text = "Actividad",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        PerfilOptionCard(
                            icon = Icons.Default.BarChart,
                            titulo = "Mis Estadísticas",
                            descripcion = "Ver tu impacto ambiental",
                            onClick = onNavigateToEstadisticas
                        )
                    }

                    item {
                        PerfilOptionCard(
                            icon = Icons.Default.EmojiEvents,
                            titulo = "Mis Logros",
                            descripcion = "Ver logros desbloqueados",
                            onClick = onNavigateToLogros
                        )
                    }

                    item {
                        PerfilOptionCard(
                            icon = Icons.Default.History,
                            titulo = "Historial de Reciclajes",
                            descripcion = "Ver todos tus reciclajes",
                            onClick = { /* TODO */ }
                        )
                    }

                    item {
                        PerfilOptionCard(
                            icon = Icons.Default.CardGiftcard,
                            titulo = "Mis Canjes",
                            descripcion = "Ver recompensas canjeadas",
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PerfilHeader(
    nombre: String,
    email: String,
    nivel: Int,
    carrera: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreenPrimary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BackgroundWhite.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nombre.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackgroundWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nombre,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BackgroundWhite
            )

            Text(
                text = email,
                fontSize = 14.sp,
                color = BackgroundWhite.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BackgroundWhite.copy(alpha = 0.2f)
            ) {
                Text(
                    text = carrera,
                    fontSize = 13.sp,
                    color = BackgroundWhite,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BackgroundWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = EcoGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar Perfil",
                    color = EcoGreenPrimary
                )
            }
        }
    }
}

@Composable
private fun EcoCoinsCard(
    ecoCoins: Long,
    nivel: Int
) {
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
                    text = "Saldo Total",
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

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BackgroundWhite.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nivel",
                        color = BackgroundWhite,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$nivel",
                        color = BackgroundWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PerfilOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = EcoGreenPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = titulo,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}