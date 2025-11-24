package com.ecocoins.campus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ecocoins.campus.navigation.*
import com.ecocoins.campus.presentation.auth.AuthViewModel
import com.ecocoins.campus.ui.theme.EcoCoinsCampusTheme
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
                    EcoCoinsAppUI()
                }
            }
        }
    }
}

@Composable
fun EcoCoinsAppUI() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsState()

    val startDestination = if (uiState.isLoggedIn) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }

    NavGraph(
        navController = navController,
        startDestination = startDestination
    )
}