package com.ecocoins.campus.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.ui.components.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authState = viewModel.authState.collectAsState().value

    // 🔥 Observa cambios del estado
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onNavigateToMain()
                viewModel.resetState()
            }
            is AuthState.Error -> {
                errorMessage = authState.message
                showErrorDialog = true
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    // Loading
    LoadingDialog(
        isLoading = authState is AuthState.Loading,
        message = "Iniciando sesión..."
    )

    // Error dialog
    ErrorDialog(
        showDialog = showErrorDialog,
        title = "Error de inicio de sesión",
        message = errorMessage,
        onDismiss = { showErrorDialog = false }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo
            Text("🌱", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))

            Text(
                text = "EcoCoins Campus",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Bienvenido de vuelta",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(48.dp))

            // Email
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
                minLines = 1
            )

            Spacer(Modifier.height(16.dp))

            // Password
            CustomPasswordTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""
                },
                label = "Contraseña",
                placeholder = "Ingresa tu contraseña",
                leadingIcon = Icons.Default.Lock,
                isError = passwordError.isNotEmpty(),
                errorMessage = passwordError,
                imeAction = ImeAction.Done,
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        if (validateFields(
                                email, password,
                                { emailError = it },
                                { passwordError = it }
                            )
                        ) {
                            viewModel.login(email, password)
                        }
                    }
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CustomTextButton(
                    text = "¿Olvidaste tu contraseña?",
                    onClick = { /* TODO */ }
                )
            }

            Spacer(Modifier.height(24.dp))

            // Login Button
            CustomButton(
                text = "Iniciar Sesión",
                onClick = {
                    if (validateFields(
                            email, password,
                            { emailError = it },
                            { passwordError = it }
                        )
                    ) {
                        viewModel.login(email, password)
                    }
                },
                enabled = authState !is AuthState.Loading
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(Modifier.weight(1f))
                Text(
                    text = "  o  ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Divider(Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            CustomOutlinedButton(
                text = "Crear Cuenta",
                onClick = onNavigateToRegister
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Al continuar, aceptas nuestros",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row {
                CustomTextButton(text = "Términos de Servicio", onClick = {})
                Text(" y ", fontSize = 12.sp)
                CustomTextButton(text = "Política de Privacidad", onClick = {})
            }
        }
    }
}


private fun validateFields(
    email: String,
    password: String,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit
): Boolean {

    var isValid = true

    if (email.isBlank()) {
        onEmailError("El correo es requerido")
        isValid = false
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("Correo inválido")
        isValid = false
    }

    if (password.isBlank()) {
        onPasswordError("La contraseña es requerida")
        isValid = false
    } else if (password.length < 6) {
        onPasswordError("Mínimo 6 caracteres")
        isValid = false
    }

    return isValid
}
