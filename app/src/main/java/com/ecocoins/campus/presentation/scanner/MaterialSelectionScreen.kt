package com.ecocoins.campus.presentation.scanner

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Colores personalizados
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val PlasticBlue = Color(0xFF2196F3)
private val PaperBrown = Color(0xFF795548)
private val GlassGreen = Color(0xFF4CAF50)
private val MetalGray = Color(0xFF607D8B)
private val BackgroundLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialSelectionScreen(
    onMaterialSelected: (TipoMaterial) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedMaterial by remember { mutableStateOf<TipoMaterial?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Selecciona el Material",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con instrucciones
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EcoGreenLight.copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )

                    Column {
                        Text(
                            text = "¿Qué vas a reciclar hoy?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text(
                            text = "Selecciona el tipo de material y escanea el código QR del contenedor",
                            fontSize = 13.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid de materiales (2x2)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MaterialCard(
                        material = TipoMaterial.PLASTICO,
                        isSelected = selectedMaterial == TipoMaterial.PLASTICO,
                        onClick = {
                            selectedMaterial = TipoMaterial.PLASTICO
                        },
                        modifier = Modifier.weight(1f)
                    )

                    MaterialCard(
                        material = TipoMaterial.PAPEL,
                        isSelected = selectedMaterial == TipoMaterial.PAPEL,
                        onClick = {
                            selectedMaterial = TipoMaterial.PAPEL
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MaterialCard(
                        material = TipoMaterial.VIDRIO,
                        isSelected = selectedMaterial == TipoMaterial.VIDRIO,
                        onClick = {
                            selectedMaterial = TipoMaterial.VIDRIO
                        },
                        modifier = Modifier.weight(1f)
                    )

                    MaterialCard(
                        material = TipoMaterial.METAL,
                        isSelected = selectedMaterial == TipoMaterial.METAL,
                        onClick = {
                            selectedMaterial = TipoMaterial.METAL
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de continuar
            AnimatedVisibility(
                visible = selectedMaterial != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Button(
                    onClick = {
                        selectedMaterial?.let { onMaterialSelected(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreenPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Escanear Código QR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MaterialCard(
    material: TipoMaterial,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
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
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(3.dp, material.color)
        } else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                // Ícono con fondo circular
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                Brush.linearGradient(
                                    colors = listOf(material.color, material.color.copy(alpha = 0.7f))
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(material.color.copy(alpha = 0.2f), material.color.copy(alpha = 0.1f))
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = material.icon,
                        contentDescription = material.nombre,
                        modifier = Modifier.size(40.dp),
                        tint = if (isSelected) Color.White else material.color
                    )
                }

                // Nombre del material
                Text(
                    text = material.nombre,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) material.color else Color(0xFF212121),
                    textAlign = TextAlign.Center
                )

                // Badge de EcoCoins
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = material.color.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = material.color
                        )
                        Text(
                            text = "+${material.ecoCoinsBase}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = material.color
                        )
                    }
                }

                // Checkmark si está seleccionado
                AnimatedVisibility(
                    visible = isSelected,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = material.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Enum con tipos de materiales
enum class TipoMaterial(
    val nombre: String,
    val icon: ImageVector,
    val color: Color,
    val ecoCoinsBase: Int,
    val ejemplos: List<String>
) {
    PLASTICO(
        nombre = "Plástico",
        icon = Icons.Default.Delete,
        color = PlasticBlue,
        ecoCoinsBase = 10,
        ejemplos = listOf("Botellas PET", "Envases", "Bolsas")
    ),
    PAPEL(
        nombre = "Papel",
        icon = Icons.Default.Description,
        color = PaperBrown,
        ecoCoinsBase = 8,
        ejemplos = listOf("Papel blanco", "Cartón", "Periódicos")
    ),
    VIDRIO(
        nombre = "Vidrio",
        icon = Icons.Default.LocalDrink,
        color = GlassGreen,
        ecoCoinsBase = 15,
        ejemplos = listOf("Botellas", "Frascos", "Envases")
    ),
    METAL(
        nombre = "Metal",
        icon = Icons.Default.Build,
        color = MetalGray,
        ecoCoinsBase = 12,
        ejemplos = listOf("Latas aluminio", "Latas acero", "Chatarra")
    )
}