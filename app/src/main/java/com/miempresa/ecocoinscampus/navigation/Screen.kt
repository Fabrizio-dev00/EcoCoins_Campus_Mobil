package com.miempresa.ecocoinscampus.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Materiales : Screen("materiales")
    object Recompensas : Screen("recompensas")
    object Perfil : Screen("perfil")
}