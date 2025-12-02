package com.ecocoins.campus.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import com.ecocoins.campus.utils.Resource

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

    val loginState by viewModel.loginState.observeAsState()

    // Observar estado de login
    LaunchedEffect(loginState) {
        when (loginState) {
            is Resource.Success -> {
                viewModel.resetLoginState()
                onNavigateToMain()
            }
            is Resource.Error -> {
                errorMessage = (loginState as Resource.Error).message ?: "Error desconocido"
                showErrorDialog = true
                viewModel.resetLoginState()
            }
            else -> {}
        }
    }

    // Loading Dialog
    LoadingDialog(
        isLoading = loginState is Resource.Loading,
        message = "Iniciando sesión..."
    )

    // Error Dialog
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
            Text(
                text = "🌱",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EcoCoins Campus",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bienvenido de vuelta",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

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

            // Password Field
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
                        if (validateFields(email, password,
                                onEmailError = { emailError = it },
                                onPasswordError = { passwordError = it }
                            )) {
                            viewModel.login(email, password)
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Olvidé mi contraseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CustomTextButton(
                    text = "¿Olvidaste tu contraseña?",
                    onClick = { /* TODO: Implementar recuperación */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            CustomButton(
                text = "Iniciar Sesión",
                onClick = {
                    if (validateFields(email, password,
                            onEmailError = { emailError = it },
                            onPasswordError = { passwordError = it }
                        )) {
                        viewModel.login(email, password)
                    }
                },
                enabled = loginState !is Resource.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text(
                    text = "  o  ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register Button
            CustomOutlinedButton(
                text = "Crear Cuenta",
                onClick = onNavigateToRegister
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "Al continuar, aceptas nuestros",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row {
                CustomTextButton(
                    text = "Términos de Servicio",
                    onClick = { /* TODO */ }
                )
                Text(
                    text = " y ",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                CustomTextButton(
                    text = "Política de Privacidad",
                    onClick = { /* TODO */ }
                )
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
        onPasswordError("La contraseña debe tener al menos 6 caracteres")
        isValid = false
    }

    return isValid
}