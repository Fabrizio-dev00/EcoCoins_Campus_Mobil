package com.ecocoins.campus.presentation.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecocoins.campus.presentation.dashboard.DashboardScreen
import com.ecocoins.campus.presentation.perfil.PerfilScreen
import com.ecocoins.campus.presentation.scanner.ScannerFlow
import com.ecocoins.campus.presentation.store.StoreScreen
import com.ecocoins.campus.presentation.history.ReciclajesHistoryScreen
import com.ecocoins.campus.presentation.history.CanjesHistoryScreen
import com.ecocoins.campus.presentation.settings.SettingsScreen
import com.ecocoins.campus.presentation.ranking.RankingScreen
import com.ecocoins.campus.presentation.logros.LogrosScreen
import com.ecocoins.campus.presentation.estadisticas.EstadisticasScreen
import com.ecocoins.campus.presentation.notificaciones.NotificacionesScreen
import com.ecocoins.campus.presentation.referidos.ReferidosScreen
import com.ecocoins.campus.presentation.mapa.MapaPuntosScreen
import com.ecocoins.campus.presentation.educacion.EducacionScreen
import com.ecocoins.campus.presentation.soporte.SoporteScreen

// Colores personalizados
private val EcoGreenPrimary = Color(0xFF2D7A3E)
private val EcoGreenLight = Color(0xFF81C784)
private val BackgroundLight = Color(0xFFF5F5F5)
private val NavUnselected = Color(0xFF9E9E9E)

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        NavigationHost(
            navController = navController,
            paddingValues = paddingValues,
            onLogout = onLogout
        )
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(BottomNavItem.Home.route) {
            DashboardScreen(
                onNavigateToReciclajes = {
                    navController.navigate(BottomNavItem.Scanner.route)
                },
                onNavigateToRecompensas = {
                    navController.navigate(BottomNavItem.Store.route)
                },
                onNavigateToPerfil = {
                    navController.navigate(BottomNavItem.Profile.route)
                },
                onNavigateToRanking = {
                    navController.navigate("ranking")
                },
                onNavigateToLogros = {
                    navController.navigate("logros")
                },
                onNavigateToEstadisticas = {
                    navController.navigate("estadisticas")
                },
                onNavigateToNotificaciones = {
                    navController.navigate("notificaciones")
                },
                onNavigateToReferidos = {
                    navController.navigate("referidos")
                },
                onNavigateToMapa = {
                    navController.navigate("mapa")
                },
                onNavigateToEducacion = {
                    navController.navigate("educacion")
                },
                onNavigateToSoporte = {
                    navController.navigate("soporte")
                }
            )
        }

        composable(BottomNavItem.Scanner.route) {
            ScannerFlow(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onComplete = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(BottomNavItem.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(BottomNavItem.Store.route) {
            StoreScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(BottomNavItem.Profile.route) {
            PerfilScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistorialReciclajes = {
                    navController.navigate("reciclajes_history")
                },
                onNavigateToMisRecompensas = {
                    navController.navigate("canjes_history")
                },
                onNavigateToConfiguracion = {
                    navController.navigate("settings")
                },
                onLogout = onLogout
            )
        }

        // ===== RUTAS FASE 1 =====

        composable("reciclajes_history") {
            ReciclajesHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("canjes_history") {
            CanjesHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditProfile = { /* TODO */ },
                onNavigateToChangePassword = { /* TODO */ },
                onNavigateToNotifications = {
                    navController.navigate("notificaciones")
                },
                onNavigateToPrivacy = { /* TODO */ },
                onNavigateToAbout = { /* TODO */ },
                onLogout = onLogout
            )
        }

        // ===== RUTAS FASE 2 - GAMIFICACIÓN =====

        composable("ranking") {
            RankingScreen { navController.popBackStack() }
        }

        composable("logros") {
            LogrosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("estadisticas") {
            EstadisticasScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ===== RUTAS FASE 3 - SOCIAL Y COMUNIDAD =====

        composable("notificaciones") {
            NotificacionesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("referidos") {
            ReferidosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("mapa") {
            MapaPuntosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ===== RUTAS FASE 4 - EDUCACIÓN Y SOPORTE =====

        composable("educacion") {
            EducacionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("soporte") {
            SoporteScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Scanner,
        BottomNavItem.Store,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(80.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    BottomNavIcon(
                        item = item,
                        isSelected = isSelected
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EcoGreenPrimary,
                    selectedTextColor = EcoGreenPrimary,
                    unselectedIconColor = NavUnselected,
                    unselectedTextColor = NavUnselected,
                    indicatorColor = EcoGreenLight.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Composable
fun BottomNavIcon(
    item: BottomNavItem,
    isSelected: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.scale(scale),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = EcoGreenLight.copy(alpha = 0.15f)
            ) {}
        }

        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            modifier = Modifier.size(26.dp)
        )
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Scanner : BottomNavItem(
        route = "scanner",
        title = "Scanner",
        selectedIcon = Icons.Filled.QrCodeScanner,
        unselectedIcon = Icons.Outlined.QrCodeScanner
    )

    object Store : BottomNavItem(
        route = "store",
        title = "Store",
        selectedIcon = Icons.Filled.Store,
        unselectedIcon = Icons.Outlined.Store
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}