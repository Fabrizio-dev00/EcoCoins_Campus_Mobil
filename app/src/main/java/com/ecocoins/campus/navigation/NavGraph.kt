package com.ecocoins.campus.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ecocoins.campus.presentation.auth.*
import com.ecocoins.campus.presentation.dashboard.DashboardScreen
import com.ecocoins.campus.presentation.reciclajes.ReciclajesScreen
import com.ecocoins.campus.presentation.recompensas.RecompensasScreen
import com.ecocoins.campus.presentation.perfil.PerfilScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // AUTH
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

        // DASHBOARD
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToReciclajes = {
                    navController.navigate(Screen.Reciclajes.route)
                },
                onNavigateToRecompensas = {
                    navController.navigate(Screen.Recompensas.route)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }

        // RECICLAJES
        composable(Screen.Reciclajes.route) {
            ReciclajesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // RECOMPENSAS
        composable(Screen.Recompensas.route) {
            RecompensasScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // PERFIL
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