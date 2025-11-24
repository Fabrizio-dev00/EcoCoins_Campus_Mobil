package com.ecocoins.campus.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Reciclajes : Screen("reciclajes")
    object Recompensas : Screen("recompensas")
    object Perfil : Screen("perfil")
}