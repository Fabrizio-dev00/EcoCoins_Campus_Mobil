package com.ecocoins.campus.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ecocoins.campus.navigation.Screen
import com.ecocoins.campus.presentation.dashboard.DashboardScreen
import com.ecocoins.campus.presentation.educacion.ContenidoDetailScreen
import com.ecocoins.campus.presentation.educacion.EducacionScreen
import com.ecocoins.campus.presentation.educacion.QuizScreen
import com.ecocoins.campus.presentation.estadisticas.EstadisticasScreen
import com.ecocoins.campus.presentation.history.ReciclajesHistoryScreen
import com.ecocoins.campus.presentation.logros.LogrosScreen
import com.ecocoins.campus.presentation.notificaciones.NotificacionesScreen
import com.ecocoins.campus.presentation.perfil.EditPerfilScreen
import com.ecocoins.campus.presentation.perfil.PerfilScreen
import com.ecocoins.campus.presentation.ranking.RankingScreen
import com.ecocoins.campus.presentation.reciclajes.ReciclajesScreen
import com.ecocoins.campus.presentation.recompensas.RecompensasScreen
import com.ecocoins.campus.presentation.scanner.*
import com.ecocoins.campus.presentation.settings.SettingsScreen

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
        topBar = {  // ⭐ NUEVO TOPBAR
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hola,",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "¡Bienvenido de vuelta!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notificaciones.route) }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones"
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Perfil.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    }
                }
            )
        },
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

            // SCANNER FLOW
            composable(Screen.QRScanner.route) {
                QRScannerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onQRDetected = { qrCode ->
                        navController.navigate(Screen.PhotoCapture.route)
                    },
                    onNavigateToManualEntry = {
                        navController.navigate(Screen.PhotoCapture.route)
                    },
                    onQRScanned = {
                        navController.navigate(Screen.MaterialSelection.route)
                    }
                )
            }

            composable(Screen.PhotoCapture.route) {
                PhotoCaptureScreen(
                    onPhotoTaken = { photoUri ->
                        navController.navigate(Screen.MaterialSelection.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.MaterialSelection.route) {
                MaterialSelectionScreen(
                    onMaterialSelected = { material ->
                        navController.navigate(Screen.AIValidation.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AIValidation.route) {
                AIValidationScreen(
                    onValidationComplete = {
                        navController.navigate(Screen.Reciclajes.route) {
                            popUpTo(Screen.Reciclajes.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Otras pantallas accesibles desde el Dashboard
            composable(Screen.ReciclajesHistory.route) {
                ReciclajesHistoryScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Recompensas.route) {
                RecompensasScreen(
                    onNavigateToDetail = { },
                    onNavigateToStore = { },
                    onNavigateToHistory = { },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Estadisticas.route) {
                EstadisticasScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Logros.route) {
                LogrosScreen(
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

            // EDUCACIÓN - Contenido y Quiz
            composable(
                route = Screen.ContenidoDetail.route,
                arguments = listOf(
                    navArgument("contenidoId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val contenidoId = backStackEntry.arguments?.getString("contenidoId") ?: ""
                ContenidoDetailScreen(
                    contenidoId = contenidoId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.Quiz.route,
                arguments = listOf(
                    navArgument("quizId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
                QuizScreen(
                    quizId = quizId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ⭐⭐⭐ PERFIL Y CONFIGURACIÓN ⭐⭐⭐
            composable(Screen.Perfil.route) {
                PerfilScreen(
                    onNavigateToEdit = { navController.navigate(Screen.EditPerfil.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToEstadisticas = { navController.navigate(Screen.Estadisticas.route) },
                    onNavigateToLogros = { navController.navigate(Screen.Logros.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.EditPerfil.route) {
                EditPerfilScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = onNavigateToLogout
                )
            }

            composable(Screen.Notificaciones.route) {
                NotificacionesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
