package com.ecocoins.campus.presentation.scanner

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

/**
 * Flujo completo del scanner que coordina todas las pantallas
 */
@Composable
fun ScannerFlow(
    onNavigateBack: () -> Unit,
    onComplete: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    var currentStep by remember { mutableStateOf(ScannerStep.MaterialSelection) }
    var selectedMaterial by remember { mutableStateOf<TipoMaterial?>(null) }
    var scannedQRCode by remember { mutableStateOf<String?>(null) }
    var capturedPhoto by remember { mutableStateOf<File?>(null) }

    val validationState by viewModel.validationState.collectAsState()

    when (currentStep) {
        ScannerStep.MaterialSelection -> {
            MaterialSelectionScreen(
                onMaterialSelected = { material ->
                    selectedMaterial = material
                    currentStep = ScannerStep.QRScanning
                },
                onNavigateBack = onNavigateBack
            )
        }

        ScannerStep.QRScanning -> {
            QRScannerScreen(
                material = selectedMaterial!!,
                onQRScanned = { qrCode ->
                    scannedQRCode = qrCode
                    currentStep = ScannerStep.PhotoCapture
                },
                onNavigateBack = {
                    currentStep = ScannerStep.MaterialSelection
                }
            )
        }

        ScannerStep.PhotoCapture -> {
            PhotoCaptureScreen(
                material = selectedMaterial!!,
                qrCode = scannedQRCode!!,
                onPhotoTaken = { photo ->
                    capturedPhoto = photo
                    currentStep = ScannerStep.AIValidation

                    // Iniciar validación con IA
                    viewModel.validateWithAI(
                        photoFile = photo,
                        material = selectedMaterial!!,
                        qrCode = scannedQRCode!!
                    )
                },
                onNavigateBack = {
                    currentStep = ScannerStep.QRScanning
                }
            )
        }

        ScannerStep.AIValidation -> {
            AIValidationScreen(
                material = selectedMaterial!!,
                photoFile = capturedPhoto!!,
                validationState = validationState,
                onNavigateBack = {
                    // Volver al inicio y resetear
                    viewModel.resetScanner()
                    currentStep = ScannerStep.MaterialSelection
                    selectedMaterial = null
                    scannedQRCode = null
                    capturedPhoto = null
                },
                onRetry = {
                    // Reintentar foto
                    currentStep = ScannerStep.PhotoCapture
                },
                onComplete = {
                    // Reciclaje exitoso
                    viewModel.resetScanner()
                    onComplete()
                }
            )
        }
    }
}

enum class ScannerStep {
    MaterialSelection,
    QRScanning,
    PhotoCapture,
    AIValidation
}