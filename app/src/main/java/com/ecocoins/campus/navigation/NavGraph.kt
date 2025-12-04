package com.ecocoins.campus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ecocoins.campus.presentation.auth.LoginScreen
import com.ecocoins.campus.presentation.auth.RegisterScreen
import com.ecocoins.campus.presentation.auth.SplashScreen
import com.ecocoins.campus.presentation.dashboard.DashboardScreen
import com.ecocoins.campus.presentation.educacion.ContenidoDetailScreen
import com.ecocoins.campus.presentation.educacion.EducacionScreen
import com.ecocoins.campus.presentation.educacion.QuizScreen
import com.ecocoins.campus.presentation.estadisticas.EstadisticasScreen
import com.ecocoins.campus.presentation.history.CanjesHistoryScreen
import com.ecocoins.campus.presentation.history.ReciclajesHistoryScreen
import com.ecocoins.campus.presentation.logros.LogrosScreen
import com.ecocoins.campus.presentation.main.MainScreen
import com.ecocoins.campus.presentation.mapa.MapaPuntosScreen
import com.ecocoins.campus.presentation.perfil.EditPerfilScreen
import com.ecocoins.campus.presentation.perfil.PerfilScreen
import com.ecocoins.campus.presentation.ranking.RankingScreen
import com.ecocoins.campus.presentation.reciclajes.ReciclajesScreen
import com.ecocoins.campus.presentation.recompensas.RecompensasDetailScreen
import com.ecocoins.campus.presentation.recompensas.RecompensasScreen
import com.ecocoins.campus.presentation.recompensas.StoreScreen
import com.ecocoins.campus.presentation.referidos.ReferidosScreen
import com.ecocoins.campus.presentation.scanner.*
import com.ecocoins.campus.presentation.settings.SettingsScreen
import com.ecocoins.campus.presentation.soporte.FAQScreen
import com.ecocoins.campus.presentation.soporte.SoporteScreen
import com.ecocoins.campus.presentation.soporte.TicketDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ====================================================================
        // AUTH SCREENS
        // ====================================================================

        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // ====================================================================
        // MAIN SCREEN (BottomNav Container)
        // ====================================================================

        composable(route = Screen.Main.route) {
            MainScreen(
                onNavigateToNotifications = {
                    // navController.navigate(Screen.Notificaciones.route)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        // ====================================================================
        // DASHBOARD
        // ====================================================================

        composable(route = Screen.Dashboard.route) {
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

        // ====================================================================
        // RECICLAJE SCREENS
        // ====================================================================

        composable(route = Screen.Reciclajes.route) {
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

        composable(route = Screen.ReciclajesHistory.route) {
            ReciclajesHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // SCANNER FLOW
        // ====================================================================

        composable(route = Screen.QRScanner.route) {
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

        composable(route = Screen.PhotoCapture.route) {
            PhotoCaptureScreen(
                onPhotoTaken = { photoUri ->
                    navController.navigate(Screen.MaterialSelection.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.MaterialSelection.route) {
            MaterialSelectionScreen(
                onMaterialSelected = { material ->
                    navController.navigate(Screen.AIValidation.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.AIValidation.route) {
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

        // ====================================================================
        // RECOMPENSAS SCREENS
        // ====================================================================

        composable(route = Screen.Recompensas.route) {
            RecompensasScreen(
                onNavigateToDetail = { recompensaId ->
                    navController.navigate(Screen.RecompensaDetail.createRoute(recompensaId))
                },
                onNavigateToStore = {
                    navController.navigate(Screen.Store.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.CanjesHistory.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.RecompensaDetail.route,
            arguments = listOf(
                navArgument("recompensaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val recompensaId = backStackEntry.arguments?.getLong("recompensaId") ?: 0L
            RecompensasDetailScreen(
                recompensaId = recompensaId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Store.route) {
            StoreScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { recompensaId ->
                    navController.navigate(Screen.RecompensaDetail.createRoute(recompensaId))
                }
            )
        }

        // ====================================================================
        // HISTORY SCREENS
        // ====================================================================

        composable(route = Screen.CanjesHistory.route) {
            CanjesHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // ESTADÍSTICAS
        // ====================================================================

        composable(route = Screen.Estadisticas.route) {
            EstadisticasScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // LOGROS
        // ====================================================================

        composable(route = Screen.Logros.route) {
            LogrosScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // RANKING
        // ====================================================================

        composable(route = Screen.Ranking.route) {
            RankingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // MAPA
        // ====================================================================

        composable(route = Screen.MapaPuntos.route) {
            MapaPuntosScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // EDUCACIÓN
        // ====================================================================

        composable(route = Screen.Educacion.route) {
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

        // ====================================================================
        // REFERIDOS
        // ====================================================================

        composable(route = Screen.Referidos.route) {
            ReferidosScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // SOPORTE
        // ====================================================================

        composable(route = Screen.Soporte.route) {
            SoporteScreen(
                onNavigateToFAQ = {
                    navController.navigate(Screen.FAQ.route)
                },
                onNavigateToTicket = { ticketId ->
                    navController.navigate(Screen.TicketDetail.createRoute(ticketId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = "faqs") {
            FAQScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.TicketDetail.route,
            arguments = listOf(
                navArgument("ticketId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getLong("ticketId") ?: 0L
            TicketDetailScreen(
                ticketId = ticketId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // PERFIL
        // ====================================================================

        composable(route = Screen.Perfil.route) {
            PerfilScreen(
                onNavigateToEdit = {
                    navController.navigate(Screen.EditPerfil.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToEstadisticas = {
                    navController.navigate(Screen.Estadisticas.route)
                },
                onNavigateToLogros = {
                    navController.navigate(Screen.Logros.route)
                }
            )
        }

        composable(route = Screen.EditPerfil.route) {
            EditPerfilScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================================
        // SETTINGS - ⭐ ACTUALIZADO CON NAVEGACIÓN A FAQs Y SOPORTE
        // ====================================================================

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onNavigateToFAQs = {                    // ⭐ NUEVO
                    navController.navigate(Screen.FAQ.route)
                },
                onNavigateToSoporte = {                 // ⭐ NUEVO
                    navController.navigate(Screen.Soporte.route)
                }
            )
        }
    }
}
