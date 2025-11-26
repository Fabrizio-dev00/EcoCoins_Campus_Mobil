package com.ecocoins.campus.presentation.scanner

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

// Colores
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCaptureScreen(
    material: TipoMaterial,
    qrCode: String,
    onPhotoTaken: (File) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var capturedImage by remember { mutableStateOf<File?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Captura del Material",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = material.nombre,
                            fontSize = 13.sp,
                            color = material.color
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (capturedImage == null) {
                // Modo cámara
                CameraView(
                    onImageCaptured = { file ->
                        capturedImage = file
                        Log.d("PhotoCapture", "✅ Foto capturada: ${file.path}")
                    },
                    onError = { exception ->
                        Log.e("PhotoCapture", "❌ Error al capturar", exception)
                    }
                )

                // Overlay con instrucciones
                PhotoCaptureOverlay(
                    material = material,
                    isProcessing = isProcessing
                )
            } else {
                // Preview de la foto capturada
                PhotoPreview(
                    imageFile = capturedImage!!,
                    material = material,
                    onConfirm = {
                        isProcessing = true
                        onPhotoTaken(capturedImage!!)
                    },
                    onRetake = {
                        capturedImage = null
                    }
                )
            }
        }
    }
}

@Composable
fun CameraView(
    onImageCaptured: (File) -> Unit,
    onError: (Exception) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var shouldCapture by remember { mutableStateOf(false) }

    // Trigger para capturar
    LaunchedEffect(shouldCapture) {
        if (shouldCapture && imageCapture != null) {
            capturePhoto(context, imageCapture!!, onImageCaptured, onError)
            shouldCapture = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = Executors.newSingleThreadExecutor()

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        Log.e("CameraView", "Error al iniciar cámara", e)
                        onError(e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Botón de captura
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            CaptureButton(
                onClick = { shouldCapture = true },
                enabled = imageCapture != null
            )
        }
    }
}

@Composable
fun CaptureButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Anillo exterior
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(4.dp, Color.White, CircleShape)
        )

        // Botón interior
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Capturar",
                tint = EcoGreenPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun PhotoCaptureOverlay(
    material: TipoMaterial,
    isProcessing: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Instrucciones en la parte superior
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = material.icon,
                        contentDescription = null,
                        tint = material.color,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Fotografía el ${material.nombre}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Asegúrate de que el material sea visible y esté bien iluminado",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Marco guía en el centro
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .border(
                    width = 2.dp,
                    color = material.color.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )
        )
    }
}

@Composable
fun PhotoPreview(
    imageFile: File,
    material: TipoMaterial,
    onConfirm: () -> Unit,
    onRetake: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen capturada
        Image(
            painter = rememberAsyncImagePainter(imageFile),
            contentDescription = "Foto capturada",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Card de confirmación
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = "¿Es esta una buena foto?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "La IA validará si el material es ${material.nombre}",
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón Reintentar
            Button(
                onClick = onRetake,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = EcoGreenPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reintentar", fontWeight = FontWeight.Bold)
            }

            // Botón Validar
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Validar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (File) -> Unit,
    onError: (Exception) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}