package com.ecocoins.campus.presentation.scanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.CustomTextField
import com.ecocoins.campus.ui.theme.*
import com.ecocoins.campus.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialSelectionScreen(
    onMaterialSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    var selectedMaterial by remember { mutableStateOf<String?>(null) }
    var peso by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("1") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Material") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Instrucciones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = EcoGreenLight.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = EcoGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Selecciona el tipo de material que vas a reciclar",
                        fontSize = 14.sp
                    )
                }
            }

            // Selección de material
            Text(
                text = "Tipo de Material",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Constants.MATERIAL_TYPES) { material ->
                    MaterialCard(
                        material = material,
                        isSelected = selectedMaterial == material,
                        onClick = { selectedMaterial = material }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Peso
            Text(
                text = "Peso (kg)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            CustomTextField(
                value = peso,
                onValueChange = { peso = it },
                label = "Peso en kilogramos",
                placeholder = "Ej: 2.5",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
                minLines = 3
            )

            // Cantidad
            Text(
                text = "Cantidad de items",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            CustomTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = "Cantidad",
                placeholder = "Ej: 5",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón continuar
            CustomButton(
                text = "Continuar",
                onClick = {
                    if (selectedMaterial != null && peso.isNotBlank()) {
                        viewModel.setSelectedMaterial(selectedMaterial!!)
                        viewModel.setPeso(peso.toDoubleOrNull() ?: 0.0)
                        viewModel.setCantidad(cantidad.toDoubleOrNull() ?: 1.0)
                        onMaterialSelected(selectedMaterial!!)
                    }
                },
                enabled = selectedMaterial != null && peso.isNotBlank()
            )
        }
    }
}

@Composable
private fun MaterialCard(
    material: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (icon, color) = when (material) {
        "PLASTICO" -> Icons.Default.Recycling to PlasticBlue
        "PAPEL" -> Icons.Default.Description to PaperBrown
        "VIDRIO" -> Icons.Default.LocalDrink to GlassGreen
        "METAL" -> Icons.Default.Build to MetalGray
        "ELECTRONICO" -> Icons.Default.PhoneAndroid to PlasticBlue
        "ORGANICO" -> Icons.Default.Grass to GlassGreen
        else -> Icons.Default.Recycling to EcoGreenSecondary
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = material,
                tint = color,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = material,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}