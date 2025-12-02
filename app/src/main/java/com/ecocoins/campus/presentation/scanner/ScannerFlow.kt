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
                onQRScanned = { navController.navigate("material_selection") },
                onNavigateToManualEntry = { navController.navigate("photo_capture") },
                onNavigateBack = onCancel
            )
        }

        composable("photo_capture") {
            PhotoCaptureScreen(
                onPhotoTaken = { navController.navigate("material_selection") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("material_selection") {
            MaterialSelectionScreen(
                onMaterialSelected = { navController.navigate("ai_validation") },
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