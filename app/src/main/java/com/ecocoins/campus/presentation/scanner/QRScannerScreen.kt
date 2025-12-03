package com.ecocoins.campus.presentation.scanner

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ecocoins.campus.ui.theme.BackgroundWhite
import com.ecocoins.campus.ui.theme.EcoGreenPrimary
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(
    onNavigateBack: () -> Unit,
    onQRDetected: (String) -> Unit,
    onNavigateToManualEntry: () -> Unit,
    onQRScanned: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var flashEnabled by remember { mutableStateOf(false) }
    var qrCodeDetected by remember { mutableStateOf(false) }

    // Permiso de cámara con Accompanist
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear Código QR") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { flashEnabled = !flashEnabled }) {
                        Icon(
                            imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = if (flashEnabled) "Apagar Flash" else "Encender Flash"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cameraPermissionState.status.isGranted) {
                // Camera Preview
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraExecutor = Executors.newSingleThreadExecutor()

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            // Aquí se integraría ML Kit para detección de QR
                            // imageAnalysis.setAnalyzer(cameraExecutor, QRCodeAnalyzer { qr ->
                            //     if (!qrCodeDetected) {
                            //         qrCodeDetected = true
                            //         onQRDetected(qr)
                            //     }
                            // })

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (exc: Exception) {
                                // Handle error
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay con marco de escaneo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val scanRectSize = minOf(canvasWidth, canvasHeight) * 0.7f
                    val left = (canvasWidth - scanRectSize) / 2
                    val top = (canvasHeight - scanRectSize) / 2

                    // Fondo oscuro
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        size = Size(canvasWidth, top)
                    )
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = Offset(0f, top + scanRectSize),
                        size = Size(canvasWidth, canvasHeight - top - scanRectSize)
                    )
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = Offset(0f, top),
                        size = Size(left, scanRectSize)
                    )
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = Offset(left + scanRectSize, top),
                        size = Size(canvasWidth - left - scanRectSize, scanRectSize)
                    )

                    // Marco de escaneo
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(left, top),
                        size = Size(scanRectSize, scanRectSize),
                        cornerRadius = CornerRadius(16f, 16f),
                        style = Stroke(width = 4f)
                    )
                }

                // Instrucciones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = EcoGreenPrimary.copy(alpha = 0.9f)
                        )
                    ) {
                        Text(
                            text = "Coloca el código QR dentro del marco",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = BackgroundWhite,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                // Mensaje de permiso denegado
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Permiso de Cámara Requerido",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Esta función necesita acceso a la cámara para escanear códigos QR",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Otorgar Permiso")
                    }
                }
            }
        }
    }
}