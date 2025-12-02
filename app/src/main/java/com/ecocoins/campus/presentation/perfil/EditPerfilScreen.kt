package com.ecocoins.campus.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.ui.components.CustomButton
import com.ecocoins.campus.ui.components.CustomTextField
import com.ecocoins.campus.ui.components.LoadingDialog
import com.ecocoins.campus.ui.components.SuccessDialog
import com.ecocoins.campus.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPerfilScreen(
    onNavigateBack: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val user by viewModel.user.observeAsState()
    val updateState by viewModel.updateState.observeAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let {
            nombre = it.nombre
            email = it.email
            carrera = it.carrera ?: ""
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is Resource.Success) {
            showSuccessDialog = true
        }
    }

    LoadingDialog(
        isLoading = updateState is Resource.Loading,
        message = "Actualizando perfil..."
    )

    SuccessDialog(
        showDialog = showSuccessDialog,
        title = "Perfil Actualizado",
        message = "Tus datos han sido actualizados correctamente",
        onDismiss = {
            showSuccessDialog = false
            viewModel.resetUpdateState()
            onNavigateBack()
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre
            CustomTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre Completo",
                placeholder = "Ingresa tu nombre",
                leadingIcon = Icons.Default.Person,
                minLines = 3
            )

            // Email (solo lectura)
            CustomTextField(
                value = email,
                onValueChange = {},
                label = "Correo Electrónico",
                placeholder = "tu@email.com",
                leadingIcon = Icons.Default.Email,
                enabled = false,
                minLines = 3
            )

            // Carrera
            CustomTextField(
                value = carrera,
                onValueChange = { carrera = it },
                label = "Carrera",
                placeholder = "Ej: Ingeniería de Sistemas",
                leadingIcon = Icons.Default.School,
                imeAction = ImeAction.Done,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón guardar
            CustomButton(
                text = "Guardar Cambios",
                onClick = {
                    user?.let {
                        val updatedUser = it.copy(
                            nombre = nombre,
                            carrera = carrera.ifBlank { null }
                        )
                        viewModel.updatePerfil(updatedUser)
                    }
                },
                enabled = nombre.isNotBlank()
            )
        }
    }
}