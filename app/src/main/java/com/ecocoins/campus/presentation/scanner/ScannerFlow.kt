package com.ecocoins.campus.presentation.scanner

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ScannerFlow(
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "qr_scanner"
    ) {
        composable("qr_scanner") {
            QRScannerScreen(
                onNavigateBack = onCancel,
                onQRDetected = { qrCode ->
                    // Cuando se detecta un QR válido, navegar a captura de foto
                    // El código QR se puede guardar en un ViewModel compartido si es necesario
                    navController.navigate("photo_capture")
                },
                onNavigateToManualEntry = {
                    // Permitir entrada manual saltando el QR
                    navController.navigate("photo_capture")
                },
                onQRScanned = {
                    // Alternativa: ir directo a selección de material
                    navController.navigate("material_selection")
                }
            )
        }

        composable("photo_capture") {
            PhotoCaptureScreen(
                onPhotoTaken = { photoUri ->
                    // Después de tomar la foto, ir a selección de material
                    navController.navigate("material_selection")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("material_selection") {
            MaterialSelectionScreen(
                onMaterialSelected = { materialData ->
                    // Después de seleccionar material, ir a validación AI
                    navController.navigate("ai_validation")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("ai_validation") {
            AIValidationScreen(
                onValidationComplete = onComplete,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}