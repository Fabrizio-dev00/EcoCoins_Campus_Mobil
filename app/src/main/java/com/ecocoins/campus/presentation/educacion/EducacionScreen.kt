package com.ecocoins.campus.presentation.educacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.ecocoins.campus.data.model.ContenidoEducativo
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.ErrorState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducacionScreen(
    onNavigateToContenido: (Long) -> Unit,
    onNavigateToQuiz: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EducacionViewModel = hiltViewModel()
) {
    val contenidos by viewModel.contenidos.observeAsState(emptyList())
    val quizzes by viewModel.quizzes.observeAsState(emptyList())
    val progreso by viewModel.progreso.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Contenidos", "Quizzes")

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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when {
                isLoading -> {
                    LoadingState(message = "Cargando contenidos...")
                }
                error != null -> {
                    ErrorState(
                        message = error ?: "Error desconocido",
                        onRetry = { viewModel.loadContenidos() }
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Progreso
                        item {
                            progreso?.let { ProgresoEducativoCard(it) }
                        }

                        when (selectedTab) {
                            0 -> {
                                // Contenidos
                                if (contenidos.isEmpty()) {
                                    item {
                                        EmptyState(
                                            icon = Icons.Default.School,
                                            title = "Sin contenidos",
                                            message = "No hay contenidos disponibles"
                                        )
                                    }
                                } else {
                                    items(contenidos) { contenido ->
                                        ContenidoCard(
                                            contenido = contenido,
                                            onClick = { onNavigateToContenido(contenido.id) }
                                        )
                                    }
                                }
                            }
                            1 -> {
                                // Quizzes
                                if (quizzes.isEmpty()) {
                                    item {
                                        EmptyState(
                                            icon = Icons.Default.Quiz,
                                            title = "Sin quizzes",
                                            message = "No hay quizzes disponibles"
                                        )
                                    }
                                } else {
                                    items(quizzes) { quiz ->
                                        QuizCard(
                                            quiz = quiz,
                                            onClick = { onNavigateToQuiz(quiz.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgresoEducativoCard(progreso: com.ecocoins.campus.data.model.ProgresoEducativo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PlasticBlue.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tu Progreso",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${progreso.contenidosCompletados}/${progreso.totalContenidos}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+${progreso.ecoCoinsGanados}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                    Text(
                        text = "EcoCoins ganados",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progreso.progresoPorcentaje.toFloat() / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = PlasticBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${String.format("%.0f", progreso.progresoPorcentaje)}% completado",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContenidoCard(
    contenido: ContenidoEducativo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            // Imagen
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = if (contenido.imagenUrl != null)
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            EcoGreenLight.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (contenido.imagenUrl != null) {
                    AsyncImage(
                        model = contenido.imagenUrl,
                        contentDescription = contenido.titulo,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = contenido.titulo,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contenido.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = contenido.descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Duración",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${contenido.duracionMinutos} min",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+${contenido.recompensaEcoCoins} EC",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoOrange
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuizCard(
    quiz: com.ecocoins.campus.data.model.Quiz,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoOrangeLight.copy(alpha = 0.2f)
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
                    .size(60.dp)
                    .background(
                        color = EcoOrange.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = "Quiz",
                    tint = EcoOrange,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quiz.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = quiz.descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Help,
                        contentDescription = "Preguntas",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${quiz.totalPreguntas} preguntas",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+${quiz.recompensaEcoCoins} EC",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoOrange
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Iniciar",
                tint = EcoOrange
            )
        }
    }
}