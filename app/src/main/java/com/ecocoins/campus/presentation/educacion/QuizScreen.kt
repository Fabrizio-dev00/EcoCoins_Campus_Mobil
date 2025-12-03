package com.ecocoins.campus.presentation.educacion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.ecocoins.campus.data.model.Pregunta
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.LoadingDialog
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    quizId: String,  // ⭐ Long -> String
    onNavigateBack: () -> Unit,
    viewModel: EducacionViewModel = hiltViewModel()
) {
    val quiz by viewModel.selectedQuiz.observeAsState()
    val resultadoQuiz by viewModel.resultadoQuiz.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    var respuestasSeleccionadas by remember { mutableStateOf<List<Int>>(emptyList()) }  // ⭐ Map -> List
    var mostrarResultado by remember { mutableStateOf(false) }

    LaunchedEffect(quizId) {
        viewModel.getQuizById(quizId)
    }

    LaunchedEffect(resultadoQuiz) {
        if (resultadoQuiz is Resource.Success) {
            mostrarResultado = true
        }
    }

    LoadingDialog(
        isLoading = resultadoQuiz is Resource.Loading,
        message = "Evaluando respuestas..."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
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
                LoadingState(message = "Cargando quiz...")
            }
            quiz == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Quiz no encontrado")
                }
            }
            mostrarResultado && resultadoQuiz is Resource.Success -> {
                ResultadoQuizScreen(
                    resultado = (resultadoQuiz as Resource.Success).data!!,
                    onFinish = {
                        viewModel.resetResultadoQuiz()
                        onNavigateBack()
                    }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header del Quiz
                    item {
                        QuizHeader(
                            titulo = quiz!!.titulo,
                            descripcion = quiz!!.descripcion,
                            totalPreguntas = quiz!!.totalPreguntas,
                            recompensa = quiz!!.recompensaEcoCoins
                        )
                    }

                    // Preguntas
                    itemsIndexed(quiz!!.preguntas) { index, pregunta ->
                        PreguntaCard(
                            numero = index + 1,
                            pregunta = pregunta,
                            respuestaSeleccionada = respuestasSeleccionadas.getOrNull(index),  // ⭐ Cambio
                            onRespuestaSelected = { opcionIndex ->
                                // ⭐ Actualizar lista en lugar de map
                                val nuevasRespuestas = respuestasSeleccionadas.toMutableList()
                                while (nuevasRespuestas.size <= index) {
                                    nuevasRespuestas.add(-1)
                                }
                                nuevasRespuestas[index] = opcionIndex
                                respuestasSeleccionadas = nuevasRespuestas
                            }
                        )
                    }

                    // Botón enviar
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        val todasRespondidas = respuestasSeleccionadas.size == quiz!!.totalPreguntas &&
                                respuestasSeleccionadas.none { it == -1 }  // ⭐ Cambio

                        CustomButton(
                            text = "Enviar Respuestas",
                            onClick = {
                                viewModel.enviarQuiz(quizId, respuestasSeleccionadas)  // ⭐ Cambio
                            },
                            enabled = todasRespondidas
                        )

                        if (!todasRespondidas) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Responde todas las preguntas para enviar",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizHeader(
    titulo: String,
    descripcion: String,
    totalPreguntas: Int,
    recompensa: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EcoOrangeLight.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = descripcion,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Help,
                        contentDescription = "Preguntas",
                        tint = EcoOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$totalPreguntas preguntas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Recompensa",
                        tint = EcoOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+$recompensa EC",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoOrange
                    )
                }
            }
        }
    }
}

@Composable
private fun PreguntaCard(
    numero: Int,
    pregunta: Pregunta,
    respuestaSeleccionada: Int?,
    onRespuestaSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Pregunta $numero",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = EcoOrange
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pregunta.pregunta,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Opciones
            pregunta.opciones.forEachIndexed { index, opcion ->
                OpcionCard(
                    opcion = opcion,
                    isSelected = respuestaSeleccionada == index,
                    onClick = { onRespuestaSelected(index) }
                )
                if (index < pregunta.opciones.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun OpcionCard(
    opcion: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) EcoGreenPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, EcoGreenPrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = EcoGreenPrimary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = opcion,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun ResultadoQuizScreen(
    resultado: com.ecocoins.campus.data.model.ResultadoQuiz,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de resultado
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = if (resultado.aprobado) EcoGreenPrimary.copy(alpha = 0.2f) else StatusRejected.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (resultado.aprobado) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = if (resultado.aprobado) "Aprobado" else "Reprobado",
                tint = if (resultado.aprobado) EcoGreenPrimary else StatusRejected,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (resultado.aprobado) "¡Felicitaciones!" else "Sigue Intentando",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (resultado.aprobado) EcoGreenPrimary else StatusRejected
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (resultado.aprobado) "Has aprobado el quiz" else "No has aprobado esta vez",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Resultados
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                ResultadoItem(
                    label = "Puntuación",
                    value = "${resultado.puntuacion}/${resultado.totalPreguntas * 10}"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ResultadoItem(
                    label = "Respuestas Correctas",
                    value = "${resultado.respuestasCorrectas}/${resultado.totalPreguntas}"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ResultadoItem(
                    label = "EcoCoins Ganados",
                    value = "+${resultado.ecoCoinsGanados} EC",
                    valueColor = EcoOrange
                )
                Spacer(modifier = Modifier.height(12.dp))
                ResultadoItem(
                    label = "Nuevo Saldo",
                    value = "${resultado.nuevoBalance} EC"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        CustomButton(
            text = "Finalizar",
            onClick = onFinish
        )
    }
}

@Composable
private fun ResultadoItem(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
