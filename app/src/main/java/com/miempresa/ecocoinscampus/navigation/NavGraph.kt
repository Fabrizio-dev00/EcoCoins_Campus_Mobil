package com.miempresa.ecocoinscampus.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.miempresa.ecocoinscampus.presentation.auth.*
import com.miempresa.ecocoinscampus.presentation.dashboard.DashboardScreen
import com.miempresa.ecocoinscampus.presentation.materiales.MaterialesScreen
import com.miempresa.ecocoinscampus.presentation.recompensas.RecompensasScreen
import com.miempresa.ecocoinscampus.presentation.perfil.PerfilScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ===================================
        // AUTH
        // ===================================
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ===================================
        // DASHBOARD
        // ===================================
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToMaterials = {
                    navController.navigate(Screen.Materiales.route)
                },
                onNavigateToRewards = {
                    navController.navigate(Screen.Recompensas.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }

        // ===================================
        // RECICLAJES
        // ===================================
        composable(Screen.Materiales.route) {
            MaterialesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ===================================
        // RECOMPENSAS
        // ===================================
        composable(Screen.Recompensas.route) {
            RecompensasScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ===================================
        // PERFIL
        // ===================================
        composable(Screen.Perfil.route) {
            PerfilScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}