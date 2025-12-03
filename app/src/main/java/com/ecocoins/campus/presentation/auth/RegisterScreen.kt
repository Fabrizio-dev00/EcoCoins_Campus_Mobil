package com.ecocoins.campus.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var carreraError by remember { mutableStateOf("") }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // ✅ CORRECTO: observeAsState con AuthState
    val registerState = viewModel.authState.collectAsState().value

    // Observar estado de registro
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is AuthState.Success -> {
                viewModel.resetState()
                onNavigateToMain()
            }
            is AuthState.Error -> {
                errorMessage = state.message
                showErrorDialog = true
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Loading Dialog
    LoadingDialog(
        isLoading = registerState is AuthState.Loading,
        message = "Creando cuenta..."
    )

    // Error Dialog
    ErrorDialog(
        showDialog = showErrorDialog,
        title = "Error de registro",
        message = errorMessage,
        onDismiss = { showErrorDialog = false }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Text(
                text = "🌱",
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Únete a EcoCoins",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Comienza tu viaje hacia un campus más verde",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nombre Field
            CustomTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = ""
                },
                label = "Nombre Completo",
                placeholder = "Ingresa tu nombre",
                leadingIcon = Icons.Default.Person,
                isError = nombreError.isNotEmpty(),
                errorMessage = nombreError,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            CustomTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                label = "Correo Institucional",
                placeholder = "ejemplo@universidad.edu",
                leadingIcon = Icons.Default.Email,
                isError = emailError.isNotEmpty(),
                errorMessage = emailError,
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Carrera Field
            CustomTextField(
                value = carrera,
                onValueChange = {
                    carrera = it
                    carreraError = ""
                },
                label = "Carrera",
                placeholder = "Ej: Ingeniería de Sistemas",
                leadingIcon = Icons.Default.School,
                isError = carreraError.isNotEmpty(),
                errorMessage = carreraError,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            CustomPasswordTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""
                },
                label = "Contraseña",
                placeholder = "Mínimo 6 caracteres",
                leadingIcon = Icons.Default.Lock,
                isError = passwordError.isNotEmpty(),
                errorMessage = passwordError,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            CustomPasswordTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = ""
                },
                label = "Confirmar Contraseña",
                placeholder = "Repite tu contraseña",
                leadingIcon = Icons.Default.Lock,
                isError = confirmPasswordError.isNotEmpty(),
                errorMessage = confirmPasswordError,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Register Button
            CustomButton(
                text = "Crear Cuenta",
                onClick = {
                    if (validateRegisterFields(
                            nombre, email, carrera, password, confirmPassword,
                            onNombreError = { nombreError = it },
                            onEmailError = { emailError = it },
                            onCarreraError = { carreraError = it },
                            onPasswordError = { passwordError = it },
                            onConfirmPasswordError = { confirmPasswordError = it }
                        )) {
                        viewModel.register(nombre, email, password, carrera)
                    }
                },
                enabled = registerState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes cuenta?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                CustomTextButton(
                    text = "Inicia Sesión",
                    onClick = onNavigateToLogin
                )
            }
        }
    }
}

private fun validateRegisterFields(
    nombre: String,
    email: String,
    carrera: String,
    password: String,
    confirmPassword: String,
    onNombreError: (String) -> Unit,
    onEmailError: (String) -> Unit,
    onCarreraError: (String) -> Unit,
    onPasswordError: (String) -> Unit,
    onConfirmPasswordError: (String) -> Unit
): Boolean {
    var isValid = true

    if (nombre.isBlank()) {
        onNombreError("El nombre es requerido")
        isValid = false
    }

    if (email.isBlank()) {
        onEmailError("El correo es requerido")
        isValid = false
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("Correo inválido")
        isValid = false
    }

    if (carrera.isBlank()) {
        onCarreraError("La carrera es requerida")
        isValid = false
    }

    if (password.isBlank()) {
        onPasswordError("La contraseña es requerida")
        isValid = false
    } else if (password.length < 6) {
        onPasswordError("La contraseña debe tener al menos 6 caracteres")
        isValid = false
    }

    if (confirmPassword.isBlank()) {
        onConfirmPasswordError("Confirma tu contraseña")
        isValid = false
    } else if (password != confirmPassword) {
        onConfirmPasswordError("Las contraseñas no coinciden")
        isValid = false
    }

    return isValid
}