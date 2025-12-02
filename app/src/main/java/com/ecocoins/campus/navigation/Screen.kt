package com.ecocoins.campus.navigation

sealed class Screen(val route: String) {
    // Auth
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main
    object Dashboard : Screen("dashboard")
    object Main : Screen("main")

    // Reciclaje
    object Reciclajes : Screen("reciclajes")
    object ReciclajesHistory : Screen("reciclajes_history")
    object QRScanner : Screen("qr_scanner")
    object PhotoCapture : Screen("photo_capture")
    object MaterialSelection : Screen("material_selection")
    object AIValidation : Screen("ai_validation")

    // Recompensas
    object Recompensas : Screen("recompensas")
    object RecompensaDetail : Screen("recompensa_detail/{recompensaId}") {
        fun createRoute(recompensaId: Long) = "recompensa_detail/$recompensaId"
    }
    object Store : Screen("store")

    // History
    object CanjesHistory : Screen("canjes_history")

    // Estadísticas
    object Estadisticas : Screen("estadisticas")

    // Logros
    object Logros : Screen("logros")

    // Ranking
    object Ranking : Screen("ranking")

    // Notificaciones
    object Notificaciones : Screen("notificaciones")

    // Mapa
    object MapaPuntos : Screen("mapa_puntos")

    // Educación
    object Educacion : Screen("educacion")
    object ContenidoDetail : Screen("contenido_detail/{contenidoId}") {
        fun createRoute(contenidoId: Long) = "contenido_detail/$contenidoId"
    }
    object Quiz : Screen("quiz/{quizId}") {
        fun createRoute(quizId: Long) = "quiz/$quizId"
    }

    // Referidos
    object Referidos : Screen("referidos")

    // Soporte
    object Soporte : Screen("soporte")
    object FAQ : Screen("faq")
    object TicketDetail : Screen("ticket_detail/{ticketId}") {
        fun createRoute(ticketId: Long) = "ticket_detail/$ticketId"
    }

    // Perfil
    object Perfil : Screen("perfil")
    object EditPerfil : Screen("edit_perfil")

    // Settings
    object Settings : Screen("settings")
}