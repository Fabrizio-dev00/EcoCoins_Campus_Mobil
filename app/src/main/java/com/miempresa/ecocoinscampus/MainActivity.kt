package com.miempresa.ecocoinscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.miempresa.ecocoinscampus.navigation.*
import com.miempresa.ecocoinscampus.presentation.auth.AuthViewModel
import com.miempresa.ecocoinscampus.ui.theme.EcoCoinsCampusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoCoinsCampusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // La llamada correcta a la función de tu UI
                    EcoCoinsAppUI()
                }
            }
        }
    }
}

// Esta es la función que contiene la lógica de navegación
@Composable
fun EcoCoinsAppUI() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsState()

    // Lógica para decidir qué pantalla mostrar primero
    val startDestination = if (uiState.isLoggedIn) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }

    // El grafo de navegación que construye las pantallas
    NavGraph(
        navController = navController,
        startDestination = startDestination
    )
}
