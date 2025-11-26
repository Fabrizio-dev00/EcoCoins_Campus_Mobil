package com.ecocoins.campus.presentation.scanner

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import java.io.File

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val EcoOrange = Color(0xFFFF9800)
private val SuccessGreen = Color(0xFF4CAF50)
private val ErrorRed = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIValidationScreen(
    material: TipoMaterial,
    photoFile: File,
    validationState: ValidationState,
    onNavigateBack: () -> Unit,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Validación con IA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (validationState is ValidationState.Error) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Preview de la foto
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(photoFile),
                        contentDescription = "Foto del material",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Estado de validación
                when (validationState) {
                    is ValidationState.Validating -> {
                        ValidatingContent(material = material)
                    }
                    is ValidationState.Success -> {
                        SuccessContent(
                            material = material,
                            ecoCoinsGanados = validationState.ecoCoinsGanados,
                            mensaje = validationState.mensaje,
                            onComplete = onComplete
                        )
                    }
                    is ValidationState.Rejected -> {
                        RejectedContent(
                            material = material,
                            razon = validationState.razon,
                            materialDetectado = validationState.materialDetectado,
                            onRetry = onRetry,
                            onNavigateBack = onNavigateBack
                        )
                    }
                    is ValidationState.Error -> {
                        ErrorContent(
                            error = validationState.mensaje,
                            onRetry = onRetry,
                            onNavigateBack = onNavigateBack
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ValidatingContent(material: TipoMaterial) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Spinner animado
        val infiniteTransition = rememberInfiniteTransition(label = "loading")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(material.color, material.color.copy(alpha = 0.5f))
                    )
                )
                .rotate(rotation),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Validando con IA...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Text(
            text = "Analizando si es ${material.nombre}",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )

        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = material.color
        )
    }
}

@Composable
fun SuccessContent(
    material: TipoMaterial,
    ecoCoinsGanados: Int,
    mensaje: String,
    onComplete: () -> Unit
) {
    // Animación de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ícono de éxito animado
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(SuccessGreen, EcoGreenLight)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Text(
                text = "¡Material Validado!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen
            )

            Text(
                text = mensaje,
                fontSize = 14.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )

            // Card de EcoCoins ganados
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EcoOrange.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "EcoCoins Ganados",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = null,
                            tint = EcoOrange,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "+$ecoCoinsGanados",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón continuar
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RejectedContent(
    material: TipoMaterial,
    razon: String,
    materialDetectado: String?,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ícono de rechazo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(ErrorRed.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(60.dp)
            )
        }

        Text(
            text = "Material No Válido",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ErrorRed
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = ErrorRed.copy(alpha = 0.05f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Razón del rechazo:",
                    fontSize = 12.sp,
                    color = Color(0xFF757575),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = razon,
                    fontSize = 14.sp,
                    color = Color(0xFF212121)
                )

                if (materialDetectado != null) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Material detectado:",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = materialDetectado,
                        fontSize = 14.sp,
                        color = Color(0xFF212121),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botones
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Intentar de Nuevo", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver al Inicio", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(80.dp)
        )

        Text(
            text = "Error de Validación",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ErrorRed
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = ErrorRed.copy(alpha = 0.05f)
            )
        ) {
            Text(
                text = error,
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reintentar", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Cancelar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Estados de validación
sealed class ValidationState {
    object Validating : ValidationState()
    data class Success(
        val ecoCoinsGanados: Int,
        val mensaje: String
    ) : ValidationState()
    data class Rejected(
        val razon: String,
        val materialDetectado: String?
    ) : ValidationState()
    data class Error(
        val mensaje: String
    ) : ValidationState()
}