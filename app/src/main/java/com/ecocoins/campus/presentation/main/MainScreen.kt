package com.ecocoins.campus.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecocoins.campus.navigation.Screen
import com.ecocoins.campus.presentation.dashboard.DashboardScreen
import com.ecocoins.campus.presentation.educacion.EducacionScreen
import com.ecocoins.campus.presentation.ranking.RankingScreen
import com.ecocoins.campus.presentation.reciclajes.ReciclajesScreen

sealed class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
) {
    object Dashboard : BottomNavItem(Screen.Dashboard.route, Icons.Default.Home, "Inicio")
    object Reciclaje : BottomNavItem(Screen.Reciclajes.route, Icons.Default.Recycling, "Reciclar")
    object Ranking : BottomNavItem(Screen.Ranking.route, Icons.Default.EmojiEvents, "Ranking")
    object Educacion : BottomNavItem(Screen.Educacion.route, Icons.Default.School, "Aprender")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Reciclaje,
        BottomNavItem.Ranking,
        BottomNavItem.Educacion
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToReciclajes = {
                        navController.navigate(Screen.Reciclajes.route)
                    },
                    onNavigateToRecompensas = {
                        navController.navigate(Screen.Recompensas.route)
                    },
                    onNavigateToEstadisticas = {
                        navController.navigate(Screen.Estadisticas.route)
                    },
                    onNavigateToLogros = {
                        navController.navigate(Screen.Logros.route)
                    }
                )
            }

            composable(Screen.Reciclajes.route) {
                ReciclajesScreen(
                    onNavigateToScanner = {
                        navController.navigate(Screen.QRScanner.route)
                    },
                    onNavigateToHistory = {
                        navController.navigate(Screen.ReciclajesHistory.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Ranking.route) {
                RankingScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Educacion.route) {
                EducacionScreen(
                    onNavigateToContenido = { contenidoId ->
                        navController.navigate(Screen.ContenidoDetail.createRoute(contenidoId))
                    },
                    onNavigateToQuiz = { quizId ->
                        navController.navigate(Screen.Quiz.createRoute(quizId))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}